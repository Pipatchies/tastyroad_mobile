package fr.tastyroad.ui.components.map

import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun DetailMapView(
    pathGeoJson: String,
    stepsGeoJson: String,
    modifier: Modifier,
) {
    val cleanPathJson = if (pathGeoJson.isBlank()) "{}" else pathGeoJson.replace("\r", "").replace("\n", " ")
    val cleanStepsJson = if (stepsGeoJson.isBlank()) "{}" else stepsGeoJson.replace("\r", "").replace("\n", " ")

    val htmlContent =
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=10.0" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body, html, #map { height: 100%; margin: 0; padding: 0; background: #ffffff; }
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
                var map = L.map('map', { zoomControl: false }).setView([46.603354, 1.888334], 6);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: 'OpenStreetMap'
                }).addTo(map);

                var polyline = L.polyline([], { color: '#E44821', weight: 5, opacity: 0.9 }).addTo(map);
                var polylineDrawn = false;
                var stepPoints = [];

                try {
                    var rawSteps = $cleanStepsJson;
                    var stepsData = (typeof rawSteps === 'string') ? JSON.parse(rawSteps) : rawSteps;

                    if (stepsData && stepsData.features && stepsData.features.length > 0) {
                        stepsData.features.forEach(function(feature, index) {
                            var coords = null;
                            if (feature.geometry && feature.geometry.coordinates) {
                                coords = feature.geometry.coordinates;
                            } else if (feature.properties && feature.properties.location && feature.properties.location.coordinates) {
                                coords = feature.properties.location.coordinates;
                            }
                            if (!coords || coords.length < 2) return;

                            stepPoints.push([coords[1], coords[0]]);

                            var isStart = (index === 0);
                            var iconHtml = isStart ? "<span style='font-size: 24px; text-shadow: 0px 2px 4px rgba(0,0,0,0.5);'>📍</span>" : ("" + index);
                            var className = isStart ? "" : "step-pin";

                            var icon = L.divIcon({
                                className: className,
                                html: iconHtml,
                                iconSize: isStart ? [24, 24] : [26, 26],
                                iconAnchor: isStart ? [12, 24] : [13, 13]
                            });

                            L.marker([coords[1], coords[0]], { icon: icon }).addTo(map);
                        });
                    }
                } catch(e) {
                    console.error("Error drawing steps:", e);
                }

                try {
                    var rawPath = $cleanPathJson;
                    var pathData = (typeof rawPath === 'string') ? JSON.parse(rawPath) : rawPath;
                    var coords = null;

                    if (pathData && pathData.coordinates && Array.isArray(pathData.coordinates) && pathData.coordinates.length > 0) {
                        coords = pathData.coordinates;
                    } else if (pathData && pathData.geometry && pathData.geometry.coordinates && Array.isArray(pathData.geometry.coordinates) && pathData.geometry.coordinates.length > 0) {
                        coords = pathData.geometry.coordinates;
                    }

                    if (coords && coords.length > 0) {
                        var routeCoords = coords.map(function(c) { return [c[1], c[0]]; });
                        polyline.setLatLngs(routeCoords);
                        map.fitBounds(polyline.getBounds(), { padding: [20, 20] });
                        polylineDrawn = true;
                    }
                } catch(e) {
                    console.error("Error drawing path:", e);
                }

                if (!polylineDrawn && stepPoints.length >= 2) {
                    polyline.setLatLngs(stepPoints);
                    map.fitBounds(polyline.getBounds(), { padding: [20, 20] });

                    var osrmCoords = stepPoints.map(function(p) { return p[1] + ',' + p[0]; }).join(';');
                    var osrmUrl = 'https://routing.openstreetmap.de/routed-foot/route/v1/foot/' + osrmCoords + '?overview=full&geometries=geojson';

                    fetch(osrmUrl)
                        .then(function(res) { return res.json(); })
                        .then(function(data) {
                            if (data && data.routes && data.routes.length > 0) {
                                var routeCoords = data.routes[0].geometry.coordinates.map(function(c) { return [c[1], c[0]]; });
                                polyline.setLatLngs(routeCoords);
                                map.fitBounds(polyline.getBounds(), { padding: [20, 20] });
                            }
                        })
                        .catch(function(err) {
                            console.error("Erreur routage OSRM detail:", err);
                        });
                }
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
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                loadDataWithBaseURL("https://tile.openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://tile.openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
        },
        modifier = modifier,
    )
}
