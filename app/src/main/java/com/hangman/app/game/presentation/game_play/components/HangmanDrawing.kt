package com.hangman.app.game.presentation.game_play.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.hangman.app.R
import com.hangman.app.core.presentation.designsystem.theme.HangmanBody
import com.hangman.app.core.presentation.designsystem.theme.HangmanGallows
import com.hangman.app.core.presentation.designsystem.theme.HangmanTheme

@Composable
fun HangmanDrawing(
    wrongGuesses: Int,
    modifier: Modifier = Modifier
) {
    val contentDesc = stringResource(R.string.cd_hangman_drawing, wrongGuesses)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .semantics { contentDescription = contentDesc }
    ) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.025f

        drawGallows(w, h, stroke)

        if (wrongGuesses >= 1) drawHead(w, h, stroke)
        if (wrongGuesses >= 2) drawBody(w, h, stroke)
        if (wrongGuesses >= 3) drawLeftArm(w, h, stroke)
        if (wrongGuesses >= 4) drawRightArm(w, h, stroke)
        if (wrongGuesses >= 5) drawLeftLeg(w, h, stroke)
        if (wrongGuesses >= 6) drawRightLeg(w, h, stroke)
    }
}

private fun DrawScope.drawGallows(w: Float, h: Float, stroke: Float) {
    val color = HangmanGallows
    // Base
    drawLine(color, Offset(w * 0.1f, h * 0.95f), Offset(w * 0.9f, h * 0.95f), stroke, StrokeCap.Round)
    // Pole
    drawLine(color, Offset(w * 0.3f, h * 0.95f), Offset(w * 0.3f, h * 0.05f), stroke, StrokeCap.Round)
    // Top beam
    drawLine(color, Offset(w * 0.3f, h * 0.05f), Offset(w * 0.65f, h * 0.05f), stroke, StrokeCap.Round)
    // Rope
    drawLine(color, Offset(w * 0.65f, h * 0.05f), Offset(w * 0.65f, h * 0.18f), stroke, StrokeCap.Round)
}

private fun DrawScope.drawHead(w: Float, h: Float, stroke: Float) {
    val radius = w * 0.08f
    val center = Offset(w * 0.65f, h * 0.18f + radius)
    drawCircle(HangmanBody, radius, center, style = Stroke(stroke))
}

private fun DrawScope.drawBody(w: Float, h: Float, stroke: Float) {
    val topY = h * 0.18f + w * 0.08f * 2
    drawLine(HangmanBody, Offset(w * 0.65f, topY), Offset(w * 0.65f, h * 0.65f), stroke, StrokeCap.Round)
}

private fun DrawScope.drawLeftArm(w: Float, h: Float, stroke: Float) {
    val shoulderY = h * 0.18f + w * 0.08f * 2 + h * 0.07f
    drawLine(HangmanBody, Offset(w * 0.65f, shoulderY), Offset(w * 0.50f, h * 0.55f), stroke, StrokeCap.Round)
}

private fun DrawScope.drawRightArm(w: Float, h: Float, stroke: Float) {
    val shoulderY = h * 0.18f + w * 0.08f * 2 + h * 0.07f
    drawLine(HangmanBody, Offset(w * 0.65f, shoulderY), Offset(w * 0.80f, h * 0.55f), stroke, StrokeCap.Round)
}

private fun DrawScope.drawLeftLeg(w: Float, h: Float, stroke: Float) {
    drawLine(HangmanBody, Offset(w * 0.65f, h * 0.65f), Offset(w * 0.50f, h * 0.82f), stroke, StrokeCap.Round)
}

private fun DrawScope.drawRightLeg(w: Float, h: Float, stroke: Float) {
    drawLine(HangmanBody, Offset(w * 0.65f, h * 0.65f), Offset(w * 0.80f, h * 0.82f), stroke, StrokeCap.Round)
}

@Preview(showBackground = true)
@Composable
private fun HangmanDrawingPreview() {
    HangmanTheme {
        HangmanDrawing(wrongGuesses = 4)
    }
}
