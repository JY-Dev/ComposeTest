package com.jydev.composetest

import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.random.Random

private const val HEART_COUNT = 1
private val moveInterpolator = FastOutSlowInEasing
private val alphaInterpolator = FastOutLinearInEasing
private val scaleInterpolator = LinearOutSlowInEasing

@Composable
fun painterFavorite() = painterResource(R.drawable.ic_baseline_favorite_24)

@Composable
fun painterFavoriteBorder() = painterResource(R.drawable.ic_baseline_favorite_border_24)
enum class AnimationState{
    START,NONE
}
private enum class BoxState {
    Collapsed,
    Expanded
}
@Composable
fun FavoriteAnimation(
    animationState: AnimationState,
    modifier: Modifier = Modifier,
) {
    val currentState by remember { mutableStateOf(animationState) }
    println("스테이트 : $currentState $animationState")
    val transition = updateTransition(currentState)
    val animatedFraction by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = if (animationState == AnimationState.START) 400 else 0,
                easing = LinearEasing
            )
        }, label = ""
    ) { state ->
        when (state) {
            AnimationState.START -> 1f
            AnimationState.NONE -> 0f
        }
    }
    println("테스트용 : $animatedFraction $currentState ${transition.targetState}")
    val hearts = remember { List(HEART_COUNT) { Heart() } }
    hearts.forEach { heart ->
        heart.reset()
    }
    FavoriteAnimationInternal(hearts,animatedFraction, animatedFraction, modifier)
}

@Composable
private fun FavoriteAnimationInternal(
    hearts: List<Heart>,
    test : Float,
    fraction: Float,
    modifier: Modifier = Modifier,
) {
    val moveProgress = moveInterpolator.transform(fraction)
    val alphaProgress = alphaInterpolator.transform(fraction)
    val scaleProgress = scaleInterpolator.transform(fraction)
    hearts.forEach { heart ->
        heart.transX = lerp(0.5f, heart.targetTransX, moveProgress)
        heart.transY = lerp(1f, heart.targetTransY, moveProgress)
        heart.alpha = lerp(1f, 0f, alphaProgress)
        heart.scale = lerp(1f, 0.6f, scaleProgress)
    }

    val drawable = rememberHeartDrawable()
    val drawableHalfWidth = drawable.intrinsicWidth / 2f
    val drawableHalfHeight = drawable.intrinsicHeight / 2f

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        hearts.forEach { heart ->
            val transX = lerp(drawableHalfWidth, width - drawableHalfWidth, heart.transX)
            val transY = lerp(drawableHalfHeight, height - drawableHalfHeight, heart.transY)
            println("테스트는 : $test $transY")
            translate(transX, transY) {
                scale(scaleX = heart.scale, scaleY = heart.scale, pivot = Offset(0.5f, 0.5f)) {
                    drawIntoCanvas { canvas ->
                        drawable.draw(canvas.nativeCanvas)
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberHeartDrawable(): Drawable {
    val context = LocalContext.current
    return remember {
        ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24)!!
            .apply {
                DrawableCompat.setTint(this, Color.Red.toArgb())

                val halfWidth = intrinsicWidth / 2
                val halfHeight = intrinsicHeight / 2
                bounds = Rect(-halfWidth, -halfHeight, halfWidth, halfHeight)
            }
    }
}

private fun lerp(from: Float, to: Float, progress: Float): Float {
    return (1 - progress) * from + to * progress
}

private data class Heart(
    var targetTransX: Float = lerp(0f, 1f, Random.nextFloat()),
    var targetTransY: Float = lerp(0f, 2 / 3f, Random.nextFloat()),
    var transX: Float = 0f,
    var transY: Float = 0f,
    var alpha: Float = 0f,
    var scale: Float = 0f,
) {
    fun reset() {
        targetTransX = lerp(0f, 1f, Random.nextFloat())
        targetTransY = lerp(0f, 2 / 3f, Random.nextFloat())
    }
}