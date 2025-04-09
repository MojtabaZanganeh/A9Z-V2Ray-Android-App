package ir.a9z.v2rayconfig.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.a9z.v2rayconfig.ui.viewmodel.MainViewModel
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import ir.a9z.v2rayconfig.utils.EnToFa
import ir.a9z.v2rayconfig.utils.formatPersianDate

@Composable
fun SubLinkScreen(viewModel: MainViewModel, navController: NavController) {
    val context = LocalContext.current
    val subLink by viewModel.subLink.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val lastUpdate by viewModel.lastUpdate.collectAsState()
    val uriHandler = LocalUriHandler.current
    val tutorialUrl = "https://a9z.ir"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading && subLink == null) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "لینک اشتراک",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            subLink?.let {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.copyToClipboard(
                            context,
                            it,
                            "لینک اشتراک کپی شد"
                        )
                    }
                ) {
                    Text(
                        text = "کپی لینک اشتراک",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } ?: run {
                if (!isLoading) {
                    Text(
                        text = "لینک اشتراک در دسترس نیست.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            lastUpdate?.let {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "تعداد پیکربندی: ${EnToFa(it.count)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "آخرین بروزرسانی: ${EnToFa(formatPersianDate(it.timestamp))}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "برای دیدن آموزش ها به سایت مراجعه کنید",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { uriHandler.openUri(tutorialUrl) }) {
                Text(
                    text = "مشاهده سایت",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            error?.let {
                if (!isLoading) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.fetchConfig() }
                    ) {
                        Text(
                            text = "تلاش مجدد",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (subLink == null) {
            viewModel.fetchSubLink()
        }
    }
} 