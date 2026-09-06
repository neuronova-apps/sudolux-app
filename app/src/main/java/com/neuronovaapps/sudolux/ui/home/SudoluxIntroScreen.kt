package com.neuronovaapps.sudolux.ui.home

import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.neuronovaapps.sudolux.R
import com.neuronovaapps.sudolux.ui.theme.sudoluxScreenContainerColor

@Composable
fun SudoluxIntroScreen(
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val background = sudoluxScreenContainerColor()
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    val chipBorder = MaterialTheme.colorScheme.outlineVariant

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 26.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1.35f))

        SudoluxAppIcon(Modifier.size(144.dp))

        Spacer(Modifier.height(24.dp))

        Text(
            text = "SUDOLUX",
            color = primary,
            fontSize = 31.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.2.sp
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Ejercita tu mente con sudoku y desafíos de lógica",
            color = muted,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal
        )

        Spacer(Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IntroChip("Lógica", chipBorder, muted)
            IntroChip("Desafíos", chipBorder, muted, Modifier.padding(horizontal = 8.dp))
            IntroChip("Sudoku", chipBorder, muted)
        }

        Spacer(Modifier.weight(1.1f))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Comenzar",
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "NeuroNova",
            color = muted.copy(alpha = 0.62f),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.8.sp
        )

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun IntroChip(
    text: String,
    borderColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun SudoluxAppIcon(
    modifier: Modifier = Modifier,
    contentDescription: String = "Icono de Sudolux"
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                adjustViewBounds = true
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(R.drawable.sudolux_intro_icon)
                this.contentDescription = contentDescription
            }
        },
        update = { imageView ->
            imageView.contentDescription = contentDescription
        }
    )
}
