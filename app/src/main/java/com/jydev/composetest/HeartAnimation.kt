package com.jydev.composetest

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun painterFavorite() = painterResource(R.drawable.ic_line_heart)

enum class AnimationState {
    START, NONE
}
enum class HeartAnimationState {
    RIGHT,CENTER, LEFT
}

@Composable
fun HeartAnimation(heart: Heart,maxWidth : Dp, maxHeight: Dp , modifier: Modifier , animationFinished : (Heart) -> Unit) {
    var heartYAnimationState by remember { mutableStateOf(AnimationState.NONE) }
    var heartXAnimationState by remember { mutableStateOf(HeartAnimationState.CENTER) }
    var heartXAnimationCount by remember { mutableStateOf(0)}
    val yFraction: Float by animateFloatAsState(
        targetValue = if (heartYAnimationState == AnimationState.START) 1f else 0f,
        animationSpec = tween(durationMillis = heart.speed, easing = FastOutSlowInEasing)
    )
    val xFraction: Float by animateFloatAsState(
        targetValue = if (heartXAnimationState == HeartAnimationState.RIGHT) 0.15f else if(heartXAnimationState == HeartAnimationState.CENTER) 0f else -0.15f,
        animationSpec = tween(durationMillis = heart.speed/4, easing = FastOutSlowInEasing)
    )
    LaunchedEffect(Unit) {
        heartYAnimationState = AnimationState.START
        heartXAnimationState = HeartAnimationState.LEFT
        heartXAnimationCount++
    }
    if(xFraction == -0.15f && heartXAnimationCount == 1){
        heartXAnimationState = HeartAnimationState.RIGHT
        heartXAnimationCount++
    } else if(xFraction == 0.15f && heartXAnimationCount == 2){
        heartXAnimationState = HeartAnimationState.LEFT
        heartXAnimationCount++
    } else if(xFraction == -0.15f && heartXAnimationCount == 3){
        heartXAnimationState = HeartAnimationState.CENTER
        heartXAnimationCount++
    } else if(xFraction == 0f && heartXAnimationCount == 4)
        animationFinished(heart)
    Icon(
        painter = painterResource(id = R.drawable.ic_opacity_heart),
        contentDescription = "",
        modifier
            .size(24.dp * heart.scale)
            .offset(x = maxWidth * xFraction, y = maxHeight - maxHeight * yFraction),
        tint = Color.White
    )
}

data class Heart(
    val scale: Float = getRandomFloatRange(0.5f, 1f),
    val speed: Int = 3000
)