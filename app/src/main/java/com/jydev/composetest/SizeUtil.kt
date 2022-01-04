package com.jydev.composetest

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPx() : Int {
    with(LocalDensity.current){
        return toPx().toInt()
    }
}