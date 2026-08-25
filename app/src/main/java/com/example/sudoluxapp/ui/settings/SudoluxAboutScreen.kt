package com.example.sudoluxapp.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.PackageInfoCompat
import com.example.sudoluxapp.R
import com.example.sudoluxapp.ui.home.SudoluxAppIcon
import java.util.Calendar

private const val PRIVACY_URL = "https://neuronova-apps.github.io/privacy/sudolux/"
private const val TERMS_URL = "https://neuronova-apps.github.io/terms/"
private const val LICENSES_URL = "https://neuronova-apps.github.io/licenses/"
private const val SUPPORT_URL = "https://neuronova-apps.github.io/support/"
private const val REPORT_ISSUE_URL = "https://neuronova-apps.github.io/support/#reportar-problema"
private const val MORE_APPS_URL = "https://neuronova-apps.github.io/apps/"

@Composable
fun SudoluxAboutScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val appInfo = remember(context) { loadAppInfo(context) }
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { AboutHeader(onBack = onBack, versionName = appInfo.versionName) }

            item {
                AboutSection("Sobre Sudolux") {
                    AboutBody(
                        "Sudolux es una aplicación desarrollada por NeuroNova Apps para disfrutar " +
                            "y practicar Sudoku mediante diferentes niveles de dificultad, desafíos " +
                            "y herramientas de apoyo durante cada partida."
                    )
                }
            }

            item {
                AboutSection("Propósito") {
                    AboutBody(
                        "Sudolux ha sido desarrollada con fines de entretenimiento y ejercitación " +
                            "mediante desafíos de lógica y razonamiento."
                    )
                }
            }

            item {
                AboutSection("Características") {
                    listOf(
                        "Diferentes niveles de dificultad.",
                        "Sistema de notas y candidatos.",
                        "Pistas durante la partida.",
                        "Seguimiento del progreso.",
                        "Opciones de personalización."
                    ).forEach { feature ->
                        BulletItem(feature)
                    }
                }
            }

            item {
                AboutSection("Accesibilidad y personalización") {
                    AboutBody(
                        "Sudolux incorpora opciones de visualización y personalización para adaptar " +
                            "la experiencia de juego a las preferencias del usuario."
                    )
                    BulletItem("Modo claro y modo oscuro.")
                    BulletItem("Alto contraste.")
                    BulletItem("Tamaño de números y candidatos.")
                    BulletItem("Reducción de animaciones.")
                }
            }

            item {
                AboutSection("Privacidad y datos") {
                    AboutBody(
                        "Consulta la información sobre privacidad y tratamiento de datos aplicable " +
                            "a Sudolux y a los servicios utilizados por la aplicación."
                    )
                    ExternalLink("Política de privacidad", PRIVACY_URL)
                }
            }

            item {
                AboutSection("Términos y condiciones") {
                    ExternalLink("Términos y condiciones", TERMS_URL)
                }
            }

            item {
                AboutSection("Licencias y atribuciones") {
                    AboutBody(
                        "Consulta las licencias, atribuciones y recursos de terceros utilizados por " +
                            "las aplicaciones de NeuroNova Apps."
                    )
                    ExternalLink("Licencias y atribuciones", LICENSES_URL)
                }
            }

            item {
                AboutSection("Soporte") {
                    AboutBody(
                        "¿Encontraste un problema o tienes alguna sugerencia? Puedes comunicarte " +
                            "con NeuroNova Apps."
                    )
                    ExternalLink("Contactar con soporte", SUPPORT_URL)
                    ExternalLink("Reportar un problema", REPORT_ISSUE_URL)
                }
            }

            item {
                AboutSection("NeuroNova Apps") {
                    ExternalLink("Más aplicaciones de NeuroNova Apps", MORE_APPS_URL)
                }
            }

            item {
                AboutSection("Información de la aplicación") {
                    AppInfoRow("Versión", appInfo.versionName)
                    AppInfoRow("Compilación", appInfo.versionCode)
                    AppInfoRow("Desarrollador", "NeuroNova Apps")
                }
            }

            item {
                AboutSection("Créditos") {
                    AboutBody("Desarrollado por NeuroNova Apps.")
                    AboutBody("Creado por Gabriel Berrospi.")
                }
            }

            item {
                Text(
                    text = "© $currentYear NeuroNova Apps. Todos los derechos reservados.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AboutHeader(onBack: () -> Unit, versionName: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(48.dp)
                    .semantics { contentDescription = "Volver a Configuración" },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = "Acerca de",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(Modifier.height(18.dp))
        SudoluxAppIcon(Modifier.size(104.dp))
        Spacer(Modifier.height(14.dp))

        Text(
            text = "Sudolux",
            fontSize = 27.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Sudoku y desafíos de lógica",
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Versión $versionName",
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
        Text(
            text = "NeuroNova Apps",
            modifier = Modifier.padding(top = 3.dp),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AboutSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
            content()
        }
    }
}

@Composable
private fun AboutBody(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        lineHeight = 21.sp
    )
}

@Composable
private fun BulletItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun ExternalLink(label: String, url: String) {
    val context = LocalContext.current

    Surface(
        onClick = { openExternalUrl(context, url) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .semantics {
                contentDescription = "$label. Abre un enlace externo"
                role = Role.Button
            },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
            Text(
                text = "↗",
                modifier = Modifier.padding(start = 10.dp),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AppInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label:",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}

private fun openExternalUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
    }

    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        showLinkError(context)
    } catch (_: SecurityException) {
        showLinkError(context)
    }
}

private fun showLinkError(context: Context) {
    Toast.makeText(
        context,
        "No se encontró una aplicación para abrir este enlace.",
        Toast.LENGTH_SHORT
    ).show()
}

private fun loadAppInfo(context: Context): AppInfo {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        AppInfo(
            versionName = packageInfo.versionName.orEmpty().ifBlank { "—" },
            versionCode = PackageInfoCompat.getLongVersionCode(packageInfo).toString()
        )
    } catch (_: PackageManager.NameNotFoundException) {
        AppInfo(versionName = "—", versionCode = "—")
    }
}

private data class AppInfo(
    val versionName: String,
    val versionCode: String
)
