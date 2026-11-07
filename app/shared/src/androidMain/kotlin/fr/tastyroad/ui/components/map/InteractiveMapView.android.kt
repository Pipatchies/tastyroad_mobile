package fr.tastyroad.ui.components.map

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
    modifier: Modifier,
) {
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
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body, html, #map { height: 100%; margin: 0; padding: 0; background: #f0ebe0; }
                .cluster-icon {
                    background-color: #E44821;
                    color: white;
                    border-radius: 50%;
                    text-align: center;
                    font-weight: bold;
                    font-family: sans-serif;
                    font-size: 16px;
                    line-height: 40px;
                    width: 40px !important;
                    height: 40px !important;
                    box-shadow: 0 2px 6px rgba(0,0,0,0.3);
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

                var clusters = [$markersJson];

                clusters.forEach(function(cluster) {
                    var customIcon = L.divIcon({
                        className: 'cluster-icon',
                        html: cluster.count
                    });

                    var marker = L.marker([cluster.lat, cluster.lng], { icon: customIcon }).addTo(map);

                    marker.on('click', function() {
                        map.setView([cluster.lat, cluster.lng], map.getZoom() + 2);
                    });
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
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                loadDataWithBaseURL("https://tile.openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier,
    )
}
