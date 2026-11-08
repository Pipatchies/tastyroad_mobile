package fr.tastyroad.ui.components.map

import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class MapCreationJSInterface(
    private val onMetricsUpdated: (
        distanceKm: Double,
        durationMinutes: Int,
        stepCount: Int,
        pathGeoJson: String,
        stepsGeoJson: String,
    ) -> Unit,
) {
    @JavascriptInterface
    fun updateMetricsAndGeometry(
        distanceKm: Double,
        durationMinutes: Int,
        stepCount: Int,
        pathGeoJson: String,
        stepsGeoJson: String,
    ) {
        onMetricsUpdated(distanceKm, durationMinutes, stepCount, pathGeoJson, stepsGeoJson)
    }
}

@Composable
actual fun CreationMapView(
    transportType: String,
    onMetricsUpdated: (distanceKm: Double, durationMinutes: Int, stepCount: Int, pathGeoJson: String, stepsGeoJson: String) -> Unit,
    modifier: Modifier,
) {
    val osrmProfile =
        when (transportType.lowercase()) {
            "vélo", "velo" -> "bike"
            "moto" -> "driving"
            else -> "foot"
        }

    val htmlContent =
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=10.0" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body, html, #map { height: 100%; margin: 0; padding: 0; background: #f0ebe0; }
                .start-pin {
                    background-color: #E44821;
                    color: white;
                    border-radius: 50%;
                    text-align: center;
                    font-weight: bold;
                    font-family: sans-serif;
                    font-size: 14px;
                    line-height: 32px;
                    width: 32px !important;
                    height: 32px !important;
                    box-shadow: 0 2px 6px rgba(0,0,0,0.4);
                }
                .step-pin {
                    background-color: #062D2A;
                    color: white;
                    border-radius: 50%;
                    text-align: center;
                    font-weight: bold;
                    font-family: sans-serif;
                    font-size: 12px;
                    line-height: 26px;
                    width: 26px !important;
                    height: 26px !important;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.3);
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', {
                    zoomControl: false,
                    touchZoom: true,
                    doubleClickZoom: true,
                    scrollWheelZoom: true
                }).setView([46.603354, 1.888334], 6);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: 'OpenStreetMap'
                }).addTo(map);

                var points = [];
                var markers = [];
                var polyline = L.polyline([], { color: '#E44821', weight: 5, opacity: 0.9 }).addTo(map);
                var profile = "$osrmProfile";

                function calculateRoute() {
                    var stepsCount = points.length > 0 ? points.length - 1 : 0;

                    if (points.length < 2) {
                        polyline.setLatLngs([]);
                        if (window.AndroidBridge) {
                            window.AndroidBridge.updateMetricsAndGeometry(0.0, 0, stepsCount, "{}", "{}");
                        }
                        return;
                    }

                    // On construit l'URL de l'API OSRM (Open Source Routing Machine)
                    var coords = points.map(p => p.lng + ',' + p.lat).join(';');
                    var url = 'https://router.project-osrm.org/route/v1/' + profile + '/' + coords + '?overview=full&geometries=geojson';

                    fetch(url)
                        .then(response => response.json())
                        .then(data => {
                            if (data.routes && data.routes.length > 0) {
                                var route = data.routes[0];
                                var routeCoords = route.geometry.coordinates.map(c => [c[1], c[0]]);
                                polyline.setLatLngs(routeCoords);

                                var totalDistKm = route.distance / 1000.0;
                                var durationMins = Math.round(route.duration / 60.0);

                                // -- GÉNÉRATION DU GEOJSON POUR L'API DJANGO --

                                // 1. Path (Ligne du tracé entier)
                                var pathGeoJson = {
                                    type: "LineString",
                                    coordinates: route.geometry.coordinates // [lng, lat]
                                };

                                // 2. Steps (FeatureCollection de Points)
                                var stepsFeatures = points.map((p, index) => {
                                    return {
                                        type: "Feature",
                                        geometry: {
                                            type: "Point",
                                            coordinates: [p.lng, p.lat]
                                        },
                                        properties: {
                                            name: (index === 0) ? "Départ" : "Étape " + index,
                                            step_order: index
                                        }
                                    };
                                });
                                var stepsGeoJson = {
                                    type: "FeatureCollection",
                                    features: stepsFeatures
                                };

                                if (window.AndroidBridge) {
                                    window.AndroidBridge.updateMetricsAndGeometry(
                                        totalDistKm,
                                        durationMins,
                                        stepsCount,
                                        JSON.stringify(pathGeoJson),
                                        JSON.stringify(stepsGeoJson)
                                    );
                                }
                            }
                        })
                        .catch(err => {
                            console.error("Erreur de routage OSRM:", err);
                        });
                }

                map.on('click', function(e) {
                    var lat = e.latlng.lat;
                    var lng = e.latlng.lng;
                    points.push({ lat: lat, lng: lng });

                    var isStart = (points.length === 1);
                    var iconHtml = isStart ? "📍" : points.length - 1;
                    var className = isStart ? "start-pin" : "step-pin";

                    var icon = L.divIcon({
                        className: className,
                        html: iconHtml
                    });

                    var marker = L.marker([lat, lng], { icon: icon }).addTo(map);
                    markers.push(marker);

                    calculateRoute();
                });
            </script>
        </body>
        </html>
        """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                addJavascriptInterface(
                    MapCreationJSInterface(onMetricsUpdated),
                    "AndroidBridge",
                )
                loadDataWithBaseURL("https://tile.openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier,
    )
}
