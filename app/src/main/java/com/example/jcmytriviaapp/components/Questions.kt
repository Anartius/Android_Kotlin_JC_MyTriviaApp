package com.example.jcmytriviaapp.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.jcmytriviaapp.model.QuestionItem
import com.example.jcmytriviaapp.screens.QuestionsViewModel
import com.example.jcmytriviaapp.utils.AppColors

@Composable
fun Questions(viewModel: QuestionsViewModel) {
    val questions = viewModel.data.value.data?.toMutableList()

    val questionIndex = remember {
        mutableStateOf(0)
    }

    if (viewModel.data.value.loading == true) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
        Log.d("Loading", "Questions: ...Loading...")
    } else {
        Log.d("Loading", "Questions: Loading STOPPED...")

        val question = try {
            questions?.get(questionIndex.value)
        } catch (e: Exception) {
            null
        }

        if (questions != null) {
            QuestionDisplay(question!!, questionIndex, viewModel) {
                questionIndex.value = questionIndex.value + 1
            }
        }
    }
}

@Composable
fun QuestionDisplay(
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionsViewModel,
    onNextClicked: (Int) -> Unit = {}
) {
    val choicesState = remember(question) {
        question.choices.toMutableList()
    }

    val answerState = remember(question) {
        mutableStateOf<Int?>(value = null)
    }

    val correctAnswerState = remember(question) {
        mutableStateOf<Boolean?>(value = null)
    }

    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            answerState.value = it
            correctAnswerState.value = choicesState[it] == question.answer
        }
    }

    var answerSelected by remember {
        mutableStateOf(false)
    }

    var scoreState by remember {
        mutableStateOf(0)
    }

    var correctChoiceState by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.mDarkPurple
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (questionIndex.value > 0) ShowProgress(score = scoreState)

            QuestionTracker(
                counter = questionIndex.value + 1,
                outOf = viewModel.getTotalQuestionCount() + 1
            )

            DrawDottedLine()

            Column {
                Text(
                    text = question.question,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                        .fillMaxHeight(0.3f),
                    fontSize = 17.sp,
                    fontWeight = Bold,
                    color = AppColors.mOffWhite,
                    lineHeight = 22.sp
                )

                choicesState.forEachIndexed { index, answerText ->
                    Row(modifier = Modifier
                        .padding(3.dp)
                        .fillMaxWidth()
                        .height(45.dp)
                        .border(
                            width = 4.dp, brush = Brush.linearGradient(
                                colors = listOf(AppColors.mOffDarkPurple, AppColors.mBlue)
                            ),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (answerState.value == index),
                            onClick = {
                                updateAnswer(index)
                                answerSelected = true
                            },
                            modifier = Modifier.padding(start = 16.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if ((correctAnswerState.value == true) &&
                                    (index == answerState.value)
                                ) {
                                    Color.Green.copy(alpha = 0.6f)
                                } else {
                                    Color.Red.copy(alpha = 0.6f)
                                }
                            )
                        )

                        val annotatedString = buildAnnotatedString {
                            withStyle(style = SpanStyle(
                                fontWeight = Light,
                                color = if (correctAnswerState.value == true &&
                                    index == answerState.value) {
                                    correctChoiceState = true
                                    Color.Green

                                } else if (correctAnswerState.value == false &&
                                    index == answerState.value) {
                                    correctChoiceState = false
                                    Color.Red

                                } else {
                                    AppColors.mOffWhite
                                },
                                fontSize = 17.sp

                            )) {
                                append(answerText)
                            }
                        }

                        Text(text = annotatedString, modifier = Modifier.padding(6.dp))
                    }
                }

                Button(onClick = {
                    if (answerSelected && correctChoiceState) {
                        onNextClicked(questionIndex.value)
                        scoreState += 1
                        answerSelected = false
                    } else if (answerSelected) {
                        onNextClicked(questionIndex.value)
                        answerSelected = false
                    }
                },
                    modifier = Modifier
                        .padding(15.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.mLightBlue
                    )
                ) {
                    Text(text = "Next",
                        modifier = Modifier.padding(4.dp),
                        color = AppColors.mOffWhite,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionTracker(
    counter: Int = 10,
    outOf: Int = 100
) {
    Text(text = buildAnnotatedString {
        withStyle(style = ParagraphStyle(textIndent = TextIndent.None)) {

            withStyle(style = SpanStyle(
                color = AppColors.mLightGray,
                fontWeight = Bold,
                fontSize = 27.sp
            )) {
                append("Question $counter/")
            }

            withStyle(style = SpanStyle(
                color = AppColors.mLightGray,
                fontWeight = Light,
                fontSize = 14.sp
            )) {
                append("$outOf")
            }
        }
    },
        modifier = Modifier.padding(20.dp)
    )
}

@Composable
fun DrawDottedLine() {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)) {
        drawLine(
            color = AppColors.mLightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}

@Composable
fun ShowProgress(score: Int = 12) {
    val gradient = Brush.linearGradient(
        listOf(Color(0xFFF95075), Color(0xFFBE6BE5))
    )

    val progressFactor by remember(score) {
        mutableStateOf(score * 0.000205f)
    }

    Surface(modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth()
        .height(45.dp)
        .border(
            width = 4.dp,
            brush = Brush.linearGradient(
                colors = listOf(AppColors.mOffDarkPurple, AppColors.mBlue)
            ),
            shape = RoundedCornerShape(34.dp)
        )
        .clip(RoundedCornerShape(50))
        .background(Color.Transparent)
    ) {
        Row(modifier = Modifier.zIndex(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(text = score.toString(),
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(23.dp))
                    .fillMaxSize()
                    .wrapContentHeight()
                    .padding(6.dp)
                    .background(Color.Transparent),
                color = AppColors.mOffWhite,
                textAlign = TextAlign.Center
            )
        }

        Row(modifier = Modifier.background(AppColors.mDarkPurple)) {
            Button(onClick = {},
                contentPadding = PaddingValues(1.dp),
                modifier = Modifier
                    .fillMaxWidth(fraction = progressFactor)
                    .background(brush = gradient),
                enabled = false,
                elevation = null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            ) {}
        }
    }
}