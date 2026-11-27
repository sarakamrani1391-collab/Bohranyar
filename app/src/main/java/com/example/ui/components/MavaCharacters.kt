package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.MavaCharacterId
import com.example.data.model.MavaEmotionType
import com.example.ui.theme.MavaCreamYellow
import com.example.ui.theme.MavaInk
import com.example.ui.theme.MavaLavender
import com.example.ui.theme.MavaMint
import com.example.ui.theme.MavaPeach
import com.example.ui.theme.MavaPink
import com.example.ui.theme.MavaSkyBlue

@Composable
fun MavaCharacterAvatar(
    character: MavaCharacterId,
    emotion: MavaEmotionType = MavaEmotionType.HAPPY,
    size: Dp = 110.dp,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "mava_char_${character.name}")
    val bob by transition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    Canvas(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = "${character.displayNameFa} - ${emotion.titleFa}" }
    ) {
        val w = this.size.width
        val h = this.size.height
        val dy = bob.dp.toPx() * 0.6f

        // Soft pastel halo behind character
        val haloColor = when (character) {
            MavaCharacterId.PUFI -> MavaPink
            MavaCharacterId.TOTO -> MavaMint
            MavaCharacterId.BOMBO -> MavaCreamYellow
            MavaCharacterId.FOFO -> MavaPeach
        }
        drawCircle(
            color = haloColor,
            radius = w * 0.45f,
            center = Offset(w * 0.5f, h * 0.54f)
        )

        when (character) {
            MavaCharacterId.PUFI -> drawPufiRabbit(w, h, dy, emotion)
            MavaCharacterId.TOTO -> drawTotoTurtle(w, h, dy, emotion)
            MavaCharacterId.BOMBO -> drawBomboBear(w, h, dy, emotion)
            MavaCharacterId.FOFO -> drawFofoFox(w, h, dy, emotion)
        }
    }
}

private fun DrawScope.drawPufiRabbit(w: Float, h: Float, dy: Float, emotion: MavaEmotionType) {
    val fur = Color(0xFFFFF8FA)
    val earInner = Color(0xFFF3B8C4)
    // Left & right floppy tall ears
    drawOval(
        color = fur,
        topLeft = Offset(w * 0.27f, h * 0.08f + dy),
        size = Size(w * 0.16f, h * 0.36f)
    )
    drawOval(
        color = earInner,
        topLeft = Offset(w * 0.31f, h * 0.14f + dy),
        size = Size(w * 0.08f, h * 0.23f)
    )
    drawOval(
        color = fur,
        topLeft = Offset(w * 0.57f, h * 0.08f + dy),
        size = Size(w * 0.16f, h * 0.36f)
    )
    drawOval(
        color = earInner,
        topLeft = Offset(w * 0.61f, h * 0.14f + dy),
        size = Size(w * 0.08f, h * 0.23f)
    )
    // Round soft face
    drawOval(
        color = fur,
        topLeft = Offset(w * 0.22f, h * 0.30f + dy),
        size = Size(w * 0.56f, h * 0.50f)
    )
    // Rosy cheeks
    drawCircle(color = MavaPink, radius = w * 0.06f, center = Offset(w * 0.33f, h * 0.62f + dy))
    drawCircle(color = MavaPink, radius = w * 0.06f, center = Offset(w * 0.67f, h * 0.62f + dy))
    // Cute nose
    drawOval(
        color = Color(0xFFE58A9B),
        topLeft = Offset(w * 0.46f, h * 0.56f + dy),
        size = Size(w * 0.08f, h * 0.055f)
    )
    drawFaceExpression(w, h, dy, emotion)
}

private fun DrawScope.drawTotoTurtle(w: Float, h: Float, dy: Float, emotion: MavaEmotionType) {
    val skin = Color(0xFFA7D7B8)
    val shell = Color(0xFF7CB69D)
    val shellRim = Color(0xFF5D9A82)
    // Cozy shell behind
    drawOval(
        color = shell,
        topLeft = Offset(w * 0.18f, h * 0.26f + dy),
        size = Size(w * 0.64f, h * 0.56f)
    )
    drawOval(
        color = shellRim,
        topLeft = Offset(w * 0.27f, h * 0.33f + dy),
        size = Size(w * 0.46f, h * 0.38f),
        style = Stroke(width = 2.dp.toPx())
    )
    // Friendly round turtle head
    drawCircle(
        color = skin,
        radius = w * 0.25f,
        center = Offset(w * 0.50f, h * 0.56f + dy)
    )
    drawCircle(color = MavaPink.copy(alpha = 0.7f), radius = w * 0.05f, center = Offset(w * 0.35f, h * 0.62f + dy))
    drawCircle(color = MavaPink.copy(alpha = 0.7f), radius = w * 0.05f, center = Offset(w * 0.65f, h * 0.62f + dy))
    drawFaceExpression(w, h, dy, emotion)
}

private fun DrawScope.drawBomboBear(w: Float, h: Float, dy: Float, emotion: MavaEmotionType) {
    val bearFur = Color(0xFFD9B28C)
    val muzzle = Color(0xFFF6E3CE)
    // Round bear ears
    drawCircle(color = bearFur, radius = w * 0.12f, center = Offset(w * 0.27f, h * 0.28f + dy))
    drawCircle(color = muzzle, radius = w * 0.065f, center = Offset(w * 0.27f, h * 0.28f + dy))
    drawCircle(color = bearFur, radius = w * 0.12f, center = Offset(w * 0.73f, h * 0.28f + dy))
    drawCircle(color = muzzle, radius = w * 0.065f, center = Offset(w * 0.73f, h * 0.28f + dy))
    // Head
    drawCircle(color = bearFur, radius = w * 0.29f, center = Offset(w * 0.50f, h * 0.54f + dy))
    // Snout
    drawOval(
        color = muzzle,
        topLeft = Offset(w * 0.36f, h * 0.54f + dy),
        size = Size(w * 0.28f, h * 0.20f)
    )
    drawOval(
        color = MavaInk,
        topLeft = Offset(w * 0.45f, h * 0.56f + dy),
        size = Size(w * 0.10f, h * 0.065f)
    )
    drawFaceExpression(w, h, dy, emotion)
}

private fun DrawScope.drawFofoFox(w: Float, h: Float, dy: Float, emotion: MavaEmotionType) {
    val foxOrange = Color(0xFFF1A87B)
    val foxCream = Color(0xFFFFF4EA)
    // Pointed soft ears
    val leftEar = Path().apply {
        moveTo(w * 0.24f, h * 0.42f + dy)
        lineTo(w * 0.22f, h * 0.14f + dy)
        lineTo(w * 0.42f, h * 0.30f + dy)
        close()
    }
    val rightEar = Path().apply {
        moveTo(w * 0.76f, h * 0.42f + dy)
        lineTo(w * 0.78f, h * 0.14f + dy)
        lineTo(w * 0.58f, h * 0.30f + dy)
        close()
    }
    drawPath(leftEar, color = foxOrange)
    drawPath(rightEar, color = foxOrange)
    // Head
    drawOval(
        color = foxOrange,
        topLeft = Offset(w * 0.20f, h * 0.28f + dy),
        size = Size(w * 0.60f, h * 0.52f)
    )
    // Cream cheek patches
    drawOval(
        color = foxCream,
        topLeft = Offset(w * 0.24f, h * 0.52f + dy),
        size = Size(w * 0.52f, h * 0.26f)
    )
    drawCircle(color = MavaInk, radius = w * 0.045f, center = Offset(w * 0.50f, h * 0.60f + dy))
    drawFaceExpression(w, h, dy, emotion)
}

private fun DrawScope.drawFaceExpression(w: Float, h: Float, dy: Float, emotion: MavaEmotionType) {
    val eyeY = h * 0.48f + dy
    when (emotion) {
        MavaEmotionType.HAPPY -> {
            drawCircle(color = MavaInk, radius = w * 0.036f, center = Offset(w * 0.39f, eyeY))
            drawCircle(color = MavaInk, radius = w * 0.036f, center = Offset(w * 0.61f, eyeY))
            drawCircle(color = Color.White, radius = w * 0.012f, center = Offset(w * 0.38f, eyeY - 2f))
            drawCircle(color = Color.White, radius = w * 0.012f, center = Offset(w * 0.60f, eyeY - 2f))
            // Cheerful smile arc
            drawArc(
                color = MavaInk,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(w * 0.42f, h * 0.62f + dy),
                size = Size(w * 0.16f, h * 0.10f),
                style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        MavaEmotionType.CALM -> {
            // Peaceful curved closed eyes
            drawArc(
                color = MavaInk,
                startAngle = 10f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(w * 0.34f, eyeY - h * 0.02f),
                size = Size(w * 0.10f, h * 0.06f),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = MavaInk,
                startAngle = 10f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(w * 0.56f, eyeY - h * 0.02f),
                size = Size(w * 0.10f, h * 0.06f),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = MavaInk,
                startAngle = 25f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(w * 0.44f, h * 0.63f + dy),
                size = Size(w * 0.12f, h * 0.07f),
                style = Stroke(width = 2.3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        MavaEmotionType.SAD -> {
            drawCircle(color = MavaInk, radius = w * 0.032f, center = Offset(w * 0.39f, eyeY))
            drawCircle(color = MavaInk, radius = w * 0.032f, center = Offset(w * 0.61f, eyeY))
            // Soft little teardrop
            drawCircle(color = MavaSkyBlue, radius = w * 0.024f, center = Offset(w * 0.36f, eyeY + h * 0.07f))
            // Gentle frown
            drawArc(
                color = MavaInk,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(w * 0.43f, h * 0.66f + dy),
                size = Size(w * 0.14f, h * 0.08f),
                style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        MavaEmotionType.SCARED -> {
            drawCircle(color = Color.White, radius = w * 0.052f, center = Offset(w * 0.39f, eyeY))
            drawCircle(color = Color.White, radius = w * 0.052f, center = Offset(w * 0.61f, eyeY))
            drawCircle(color = MavaInk, radius = w * 0.028f, center = Offset(w * 0.39f, eyeY))
            drawCircle(color = MavaInk, radius = w * 0.028f, center = Offset(w * 0.61f, eyeY))
            // Small round O mouth
            drawCircle(
                color = MavaInk,
                radius = w * 0.035f,
                center = Offset(w * 0.50f, h * 0.68f + dy),
                style = Stroke(width = 2.2.dp.toPx())
            )
        }
    }
}
