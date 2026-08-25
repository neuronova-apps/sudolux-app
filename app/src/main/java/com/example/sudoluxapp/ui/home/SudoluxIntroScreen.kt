package com.example.sudoluxapp.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SudoluxIntroScreen(
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.background
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    val chipBorder = MaterialTheme.colorScheme.outlineVariant

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 26.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1.55f))

        SudokuIntroMark(primary = primary)

        Spacer(Modifier.height(28.dp))

        Text(
            text = "SUDOLUX",
            color = primary,
            fontSize = 31.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.2.sp
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Ejercita tu mente con\nsudoku y lógica",
            color = muted,
            fontSize = 22.sp,
            lineHeight = 29.sp,
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
            IntroChip("Concentración", chipBorder, muted)
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
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
private fun SudokuIntroMark(primary: Color) {
    val outerShape = RoundedCornerShape(24.dp)
    val cellShape = RoundedCornerShape(7.dp)
    val cellBorder = primary.copy(alpha = 0.30f)
    val markBackground = primary.copy(alpha = 0.035f)

    Column(
        modifier = Modifier
            .size(116.dp)
            .clip(outerShape)
            .background(markBackground)
            .border(1.5.dp, primary.copy(alpha = 0.34f), outerShape)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SudokuMarkCell("1", primary, cellBorder, cellShape)
            SudokuMarkCell("6", primary, cellBorder, cellShape)
            SudokuMarkCell("9", primary, cellBorder, cellShape)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SudokuMarkCell("5", primary, cellBorder, cellShape)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SudokuMarkCell("4", primary, cellBorder, cellShape)
            SudokuMarkCell("8", primary, cellBorder, cellShape)
            SudokuMarkCell("2", primary, cellBorder, cellShape)
        }
    }
}

@Composable
private fun SudokuMarkCell(
    value: String,
    primary: Color,
    borderColor: Color,
    shape: RoundedCornerShape
) {
    Box(
        modifier = Modifier
            .size(27.dp)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, borderColor, shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            color = primary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black
        )
    }
}
