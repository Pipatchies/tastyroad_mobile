package fr.tastyroad.ui.components.metrics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MetricsBox(
    metric1Value: String,
    metric1Label: String,
    metric2Value: String,
    metric2Label: String,
    metric3Value: String,
    metric3Label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MetricColumnItem(value = metric1Value, label = metric1Label, modifier = Modifier.weight(1f))

            VerticalDivider(
                modifier = Modifier.height(52.dp),
                color = MaterialTheme.colorScheme.primary,
                thickness = 1.dp,
            )

            MetricColumnItem(value = metric2Value, label = metric2Label, modifier = Modifier.weight(1f))

            VerticalDivider(
                modifier = Modifier.height(52.dp),
                color = MaterialTheme.colorScheme.primary,
                thickness = 1.dp,
            )

            MetricColumnItem(value = metric3Value, label = metric3Label, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MetricColumnItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
