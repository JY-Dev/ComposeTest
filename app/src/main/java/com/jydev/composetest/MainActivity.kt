package com.jydev.composetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jydev.composetest.ui.theme.ComposeTestTheme

class MainActivity : ComponentActivity() {
    var messageList = mutableListOf<String>()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                val keyboardState by keyboardAsState()
                var heartCount by remember { mutableStateOf(0) }
                val hearts = remember { mutableStateListOf<Heart>() }
                val focusManager = LocalFocusManager.current
                Surface(color = Color.Yellow) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painterResource(R.drawable.background),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Row(
                            Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp)
                        ) {
                            ChatView(
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.Bottom),
                                keyboardState,
                                focusManager
                            )
                            AnimatedVisibility(visible = keyboardState == KeyboardState.Closed) {
                                HeartEffectView(
                                    modifier = Modifier.size(
                                        width = 76.dp,
                                        height = 200.dp
                                    ), hearts = hearts, heartCount
                                ) {
                                    hearts.add(Heart())
                                    heartCount++
                                }
                            }
                            if (keyboardState == KeyboardState.Opened)
                                hearts.clear()

                        }
                    }
                }
            }
        }
    }

    private fun addMessage(chatMessage: String) {
        messageList.add(chatMessage)
        MessageHolder.setMessageList(messageList.toList())
    }

    @Composable
    fun HeartEffectView(
        modifier: Modifier,
        hearts: List<Heart>,
        heartCount: Int,
        heartClick: () -> Unit
    ) {
        BoxWithConstraints(modifier) {
            HeartAnimationView(hearts = hearts)
            Column(Modifier.align(Alignment.BottomCenter)) {
                HeartIconView(
                    modifier = Modifier
                        .size(width = 24.dp, height = 20.dp),
                    heartClick
                )
                HeartCountText(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp,), heartCount = heartCount,
                )
            }
        }
    }

    @Composable
    fun HeartAnimationView(hearts: List<Heart>) {
        repeat(hearts.size) {
            HeartAnimation(hearts[it])
        }
    }

    @Composable
    fun HeartIconView(modifier: Modifier, heartClick: () -> Unit) {
        Icon(painter = painterFavorite(), "", modifier = modifier.noRippleClickable {
            heartClick()
        }, tint = Color.White)
    }

    @Composable
    fun HeartCountText(modifier: Modifier,heartCount: Int) {
        Text(
            text = heartCount.toString(),
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
        )
    }

    @Composable
    fun ChatView(modifier: Modifier, keyboardState: KeyboardState, focusManager: FocusManager) {
        var chatMessage by remember { mutableStateOf("") }
        Column(modifier = modifier) {
            ChatList(
                Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 22.dp)
            )
            ChatTextField(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = if (keyboardState == KeyboardState.Opened) 20.dp else 0.dp
                    ),
                chatMessage,
                onMessageChange = {
                    chatMessage = it
                },
                keyboardDone = {
                    addMessage(chatMessage = chatMessage)
                    chatMessage = ""
                    focusManager.clearFocus()
                })
        }
    }

    @Composable
    fun ChatList(modifier: Modifier) {
        val messageList by MessageHolder.messageList.observeAsState(listOf())
        LazyColumn(modifier = modifier
            .background(Color.Transparent)
            .fillMaxWidth(), content = {
            val messageSubList = if (messageList.size > 4)
                messageList.subList(messageList.size - 4, messageList.size)
            else messageList

            items(messageSubList) { message ->
                Row(modifier = Modifier.padding(start = 20.dp, top = 12.dp)) {
                    Text(
                        text = "테스트",
                        modifier = Modifier.padding(end = 10.dp),
                        color = Color(0xfff2f2f2),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = message,
                        color = Color(0xfff2f2f2)
                    )
                }

            }
        })
    }

    @Composable
    fun ChatTextField(
        modifier: Modifier,
        message: String,
        onMessageChange: (String) -> Unit,
        keyboardDone: () -> Unit
    ) {
        BasicTextField(
            modifier = modifier
                .border(
                    width = 2.dp,
                    color = Color(0xfff2f2f2),
                    shape = RoundedCornerShape(75.dp)
                )
                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
            placeholderText = "댓글 작성하기",
            message = message,
            onMessageChange = onMessageChange,
            keyboardDone = keyboardDone)
    }

    @Composable
    private fun BasicTextField(
        modifier: Modifier = Modifier,
        placeholderText: String = "Placeholder",
        fontSize: TextUnit = 14.sp,
        message: String,
        onMessageChange: (String) -> Unit,
        keyboardDone: () -> Unit
    ) {
        BasicTextField(
            modifier = modifier
                .fillMaxWidth(),
            value = message,
            onValueChange = onMessageChange,
            keyboardActions = KeyboardActions(onDone = { keyboardDone() }),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (message.isEmpty()) Text(
                    placeholderText,
                    color = Color(0xfff2f2f2),
                    fontSize = fontSize
                )
                Text(text = message, color = Color(0xfff2f2f2), fontSize = fontSize)
            },
        )
    }
}


