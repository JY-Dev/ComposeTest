package com.jydev.composetest

fun getRandomFloatRange(start : Float, end : Float) : Float =
    Math.round((start + Math.random() * (end - start))*100)/100f
