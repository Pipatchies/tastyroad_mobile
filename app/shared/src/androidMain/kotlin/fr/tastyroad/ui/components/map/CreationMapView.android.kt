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
                var map = L.map('map', {
                    zoomControl: false,
                    touchZoom: 'center',
                    doubleClickZoom: true,
                    scrollWheelZoom: true,
                    dragging: true,
                    tap: true
                }).setView([46.603354, 1.888334], 6);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: 'OpenStreetMap'
                }).addTo(map);

                var points = [];
                var markers = [];
                var polyline = L.polyline([], { color: '#E44821', weight: 5, opacity: 0.9 }).addTo(map);
                var profile = "$osrmProfile";

                function getSpeedMultiplier() {
                    if (profile === 'driving') return 1.0;
                    if (profile === 'bike') return 3.33;
                    if (profile === 'foot') return 10.0;
                    return 1.0;
                }

                function updateProfile(newProfile) {
                    if (profile !== newProfile) {
                        profile = newProfile;
                        calculateRoute();
                    }
                }

                function calculateRoute() {
                    var stepsCount = points.length > 0 ? points.length - 1 : 0;

                    if (points.length < 2) {
                        polyline.setLatLngs([]);
                        if (window.AndroidBridge) {
                            window.AndroidBridge.updateMetricsAndGeometry(0.0, 0, stepsCount, "{}", "{}");
                        }
                        return;
                    }


                    var directCoords = points.map(function(p) { return [p.lat, p.lng]; });
                    polyline.setLatLngs(directCoords);

                    function sendFallbackGeometry() {
                        var lineCoordinates = points.map(function(p) { return [p.lng, p.lat]; });
                        var pathGeoJson = {
                            type: "LineString",
                            coordinates: lineCoordinates
                        };

                        var stepsFeatures = points.map(function(p, index) {
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

                        var distKm = 0.0;
                        for (var i = 0; i < points.length - 1; i++) {
                            var p1 = points[i];
                            var p2 = points[i+1];
                            var R = 6371;
                            var dLat = (p2.lat - p1.lat) * Math.PI / 180;
                            var dLng = (p2.lng - p1.lng) * Math.PI / 180;
                            var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                                    Math.cos(p1.lat * Math.PI / 180) * Math.cos(p2.lat * Math.PI / 180) *
                                    Math.sin(dLng/2) * Math.sin(dLng/2);
                            var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                            distKm += R * c;
                        }

                        var speed = (profile === 'driving') ? 50.0 : (profile === 'bike' ? 15.0 : 4.5);
                        var durationMins = Math.round((distKm / speed) * 60.0);

                        if (window.AndroidBridge) {
                            window.AndroidBridge.updateMetricsAndGeometry(
                                distKm,
                                durationMins,
                                stepsCount,
                                JSON.stringify(pathGeoJson),
                                JSON.stringify(stepsGeoJson)
                            );
                        }
                    }

                    var coords = points.map(function(p) { return p.lng + ',' + p.lat; }).join(';');
                    var url = 'https://router.project-osrm.org/route/v1/' + profile + '/' + coords + '?overview=full&geometries=geojson';

                    fetch(url)
                        .then(function(response) { return response.json(); })
                        .then(function(data) {
                            if (data && data.routes && data.routes.length > 0) {
                                var route = data.routes[0];
                                var routeCoords = route.geometry.coordinates.map(function(c) { return [c[1], c[0]]; });
                                polyline.setLatLngs(routeCoords);

                                var totalDistKm = route.distance / 1000.0;
                                var durationMins = Math.round((route.duration * getSpeedMultiplier()) / 60.0);

                                var pathGeoJson = {
                                    type: "LineString",
                                    coordinates: route.geometry.coordinates
                                };

                                var stepsFeatures = points.map(function(p, index) {
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
                            } else {
                                sendFallbackGeometry();
                            }
                        })
                        .catch(function(err) {
                            console.error("Erreur de routage OSRM:", err);
                            sendFallbackGeometry();
                        });
                }

                map.on('click', function(e) {
                    var lat = e.latlng.lat;
                    var lng = e.latlng.lng;
                    points.push({ lat: lat, lng: lng });

                    var markerIndex = points.length - 1;
                    var isStart = (points.length === 1);
                    var iconHtml = isStart ? "<span style='font-size: 24px; text-shadow: 0px 2px 4px rgba(0,0,0,0.5);'>📍</span>" : ("" + markerIndex);
                    var className = isStart ? "" : "step-pin";

                    var icon = L.divIcon({
                        className: className,
                        html: iconHtml,
                        iconSize: isStart ? [24, 24] : [26, 26],
                        iconAnchor: isStart ? [12, 24] : [13, 13]
                    });

                    var marker = L.marker([lat, lng], { icon: icon }).addTo(map);

                    marker.on('click', function(e) {
                        L.DomEvent.stopPropagation(e);

                        var index = markers.indexOf(marker);
                        if (index > -1) {

                            if (index === 0 && points.length >= 3) {
                                console.log("Fermeture de la boucle détectée !");
                                var startPoint = points[0];
                                points.push({ lat: startPoint.lat, lng: startPoint.lng });

                                var newIndex = points.length - 1;
                                var loopIcon = L.divIcon({
                                    className: "step-pin",
                                    html: "" + newIndex,
                                    iconSize: [26, 26],
                                    iconAnchor: [13, 13]
                                });
                                var loopMarker = L.marker([startPoint.lat, startPoint.lng], { icon: loopIcon }).addTo(map);

                                loopMarker.on('click', function(ev) {
                                    L.DomEvent.stopPropagation(ev);
                                    var loopIdx = markers.indexOf(loopMarker);
                                    if(loopIdx > -1) {
                                        points.splice(loopIdx, 1);
                                        map.removeLayer(loopMarker);
                                        markers.splice(loopIdx, 1);
                                        calculateRoute();
                                    }
                                });

                                markers.push(loopMarker);
                                calculateRoute();
                                return;
                            }

                            points.splice(index, 1);

                            map.removeLayer(marker);

                            markers.splice(index, 1);

                            markers.forEach(function(m, i) {
                                var mIsStart = (i === 0);
                                var mIconHtml = mIsStart ? "<span style='font-size: 24px; text-shadow: 0px 2px 4px rgba(0,0,0,0.5);'>📍</span>" : ("" + i);
                                var mClassName = mIsStart ? "" : "step-pin";

                                m.setIcon(L.divIcon({
                                    className: mClassName,
                                    html: mIconHtml,
                                    iconSize: mIsStart ? [24, 24] : [26, 26],
                                    iconAnchor: mIsStart ? [12, 24] : [13, 13]
                                }));
                            });

                            calculateRoute();
                        }
                    });

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
        update = { webView ->
            val updatedOsrmProfile =
                when (transportType.lowercase()) {
                    "vélo", "velo" -> "bike"
                    "moto" -> "driving"
                    else -> "foot"
                }
            webView.evaluateJavascript("if (typeof updateProfile === 'function') { updateProfile('$updatedOsrmProfile'); }", null)
        },
        modifier = modifier,
    )
}
