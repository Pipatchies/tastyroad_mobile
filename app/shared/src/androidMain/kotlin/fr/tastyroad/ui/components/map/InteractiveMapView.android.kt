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
actual fun InteractiveMapView(
    clusters: List<MapClusterData>,
    centerLat: Double?,
    centerLng: Double?,
    zoomLevel: Int?,
    modifier: Modifier,
) {
    val initialLat = centerLat ?: 46.603354
    val initialLng = centerLng ?: 1.888334
    val initialZoom = zoomLevel ?: 6

    val markersJson =
        clusters.joinToString(",") { cluster ->
            "{ lat: ${cluster.latitude}, lng: ${cluster.longitude}, count: ${cluster.count}, name: '${cluster.name.replace("'", "\\'")}' }"
        }

    val htmlContent =
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet.markercluster@1.4.1/dist/MarkerCluster.css" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet.markercluster@1.4.1/dist/MarkerCluster.Default.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <script src="https://unpkg.com/leaflet.markercluster@1.4.1/dist/leaflet.markercluster.js"></script>
            <style>
                body, html, #map { height: 100%; margin: 0; padding: 0; background: #ffffff; }
                .marker-cluster-custom {
                    background-color: rgba(228, 72, 33, 0.6);
                }
                .marker-cluster-custom div {
                    background-color: rgba(228, 72, 33, 0.9);
                    color: white;
                    font-weight: bold;
                    font-family: sans-serif;
                }
                .single-marker {
                    background-color: #E44821;
                    border-radius: 50%;
                    border: 2px solid white;
                    width: 20px !important;
                    height: 20px !important;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.3);
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', { zoomControl: false }).setView([$initialLat, $initialLng], $initialZoom);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: 'OpenStreetMap'
                }).addTo(map);

                var markersGroup = L.markerClusterGroup({
                    iconCreateFunction: function(cluster) {
                        return new L.DivIcon({
                            html: '<div><span>' + cluster.getChildCount() + '</span></div>',
                            className: 'marker-cluster marker-cluster-custom',
                            iconSize: new L.Point(40, 40)
                        });
                    }
                });

                var clusters = [$markersJson];

                clusters.forEach(function(cluster) {
                    var customIcon = L.divIcon({
                        className: 'single-marker',
                        html: ""
                    });

                    var marker = L.marker([cluster.lat, cluster.lng], { icon: customIcon });
                    markersGroup.addLayer(marker);
                });

                map.addLayer(markersGroup);

                function setMapView(lat, lng, zoom) {
                    if (typeof map !== 'undefined') {
                        map.setView([lat, lng], zoom);
                    }
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
            if (centerLat != null && centerLng != null) {
                val zoom = zoomLevel ?: 11
                webView.evaluateJavascript("if (typeof setMapView === 'function') { setMapView($centerLat, $centerLng, $zoom); }", null)
            }
        },
        modifier = modifier,
    )
}
