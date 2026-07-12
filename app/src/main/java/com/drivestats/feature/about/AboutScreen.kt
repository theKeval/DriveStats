package com.drivestats.feature.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BorderStroke
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivestats.BuildConfig
import com.drivestats.R
import com.drivestats.ui.theme.DriveStatsTheme

private val AboutSurface = Color(0xFF0E1116)
private val AboutSurfaceContainer = Color(0xFF161B22)
private val AboutSurfaceContainerHigh = Color(0xFF1E2530)
private val AboutSurfaceContainerHighest = Color(0xFF252D3A)
private val AboutOnSurface = Color(0xFFE3E6EA)
private val AboutOnSurfaceVariant = Color(0xFFA6ADB8)
private val AboutOutline = Color(0xFF3A414C)
private val AboutOutlineVariant = Color(0xFF232933)
private val AboutPrimary = Color(0xFF00E5B3)
private val AboutPrimaryContainer = Color(0xFF00513C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Scaffold(
        containerColor = AboutSurface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.about_title),
                        color = AboutOnSurface,
                        fontSize = 28.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Medium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_about_back_arrow),
                            contentDescription = stringResource(R.string.back),
                            tint = Color.Unspecified,
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(AboutPrimary.copy(alpha = 0.06f), AboutSurface),
                        radius = 900f,
                    ),
                )
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Spacer(Modifier.height(32.dp))
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = AboutPrimaryContainer,
            )
            {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.ic_about_logo_mark),
                    contentDescription = stringResource(R.string.about_logo_content_description),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.app_name),
                color = AboutOnSurface,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AboutSurfaceContainerHigh,
                border = BorderStroke(1.dp, AboutOutline),
            ) {
                Text(
                    text = stringResource(
                        R.string.about_version,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE,
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = AboutOnSurfaceVariant,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.height(13.dp))
            Text(
                text = stringResource(R.string.about_description),
                modifier = Modifier.widthIn(max = 280.dp),
                textAlign = TextAlign.Center,
                color = AboutOnSurfaceVariant,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Normal,
            )
            Spacer(Modifier.height(21.dp))
            HorizontalDivider(color = AboutOutlineVariant, thickness = 1.dp)
            Spacer(Modifier.height(11.dp))
            AboutGitHubRow(
                onClick = { uriHandler.openUri(stringResource(R.string.about_github_url)) },
            )
            Spacer(Modifier.height(19.dp))
            Text(
                text = stringResource(R.string.about_footer),
                textAlign = TextAlign.Center,
                color = AboutOnSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun AboutGitHubRow(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val containerColor = if (pressed) AboutSurfaceContainerHighest else AboutSurfaceContainer

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(width = 1.dp, color = AboutOutline, shape = RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            color = AboutSurfaceContainerHigh,
            shape = RoundedCornerShape(8.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_about_github),
                contentDescription = null,
                modifier = Modifier.padding(6.dp).size(28.dp),
                tint = Color.Unspecified,
            )
        }
        Text(
            text = stringResource(R.string.about_view_source),
            modifier = Modifier.weight(1f),
            color = AboutOnSurface,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
        )
        Icon(
            painter = painterResource(R.drawable.ic_about_chevron_right),
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutScreenPreview() {
    DriveStatsTheme {
        AboutScreen(onBack = {})
    }
}
