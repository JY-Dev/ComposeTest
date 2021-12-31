package com.jydev.composetest

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import java.lang.Math.round


@Composable
fun MainScreen() { // 이 함수가 뭘 하는지 지금은 **몰라도 됨**
    val transition = rememberInfiniteTransition()
    val value by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = { round(it).toFloat() }),
            repeatMode = RepeatMode.Reverse
        )
    )

    WhatIsRemember(value)
}

@Composable
fun WhatIsRemember(value: Float) { // 이 함수에만 집중!!
    Column {
        Text(text = value.toString())
    }
}