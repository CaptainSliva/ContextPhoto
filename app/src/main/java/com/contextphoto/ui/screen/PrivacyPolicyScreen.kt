package com.contextphoto.ui.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.contextphoto.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreenWithScaffold(showConfidence: MutableState<Boolean>) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.privacy_policy_title))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        backActions(showConfidence)
                    }) {
                        Icon(
                            Icons.Default.ArrowBack, // Кнопка назад
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        content = { paddingValues ->
            BackHandler {
                backActions(showConfidence)
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(paddingValues)
                        .padding(horizontal = 8.dp)
                        .background(MaterialTheme.colorScheme.background),
            ) {
                val lines = PrivacyPolicyText.FULL_TEXT.split("\n")

                lines.forEach { line ->
                    when {
                        // Жирный текст
                        line.startsWith("**") && line.endsWith("**") -> {
                            Text(
                                text = line.replace("**", ""),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                            )
                        }

                        // Обработка ссылок
                        line.contains("[") && line.contains("](https") -> {
                            val annotatedString =
                                buildAnnotatedString {
                                    var currentIndex = 0
                                    val pattern = "\\[(.*?)\\]\\((.*?)\\)".toRegex()
                                    val matches = pattern.findAll(line)

                                    matches.forEach {
                                        val beforeText = line.substring(currentIndex, it.range.first)
                                        append(beforeText)

                                        val linkText = it.groupValues[1]
                                        val linkUrl = it.groupValues[2]

                                        pushStringAnnotation(tag = "URL", annotation = linkUrl)
                                        withStyle(
                                            style =
                                                SpanStyle(
                                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                                    textDecoration = TextDecoration.Underline,
                                                ),
                                        ) {
                                            append(linkText)
                                        }
                                        pop()

                                        currentIndex = it.range.last + 1
                                    }

                                    // Оставшийся текст
                                    if (currentIndex < line.length) {
                                        append(line.substring(currentIndex))
                                    }
                                }

                            ClickableText(
                                text = annotatedString,
                                onClick = { offset ->
                                    annotatedString
                                        .getStringAnnotations("URL", offset, offset)
                                        .firstOrNull()
                                        ?.let {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                },
                                style =
                                    TextStyle(
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    ),
                                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp),
                            )
                        }

                        // Пункты с маркерами
                        line.trim().startsWith("•") -> {
                            Text(
                                text = line,
                                modifier = Modifier.padding(start = 16.dp, top = 2.dp, bottom = 2.dp),
                            )
                        }

                        // Пустые строки
                        line.isBlank() -> {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Обычный текст
                        else -> {
                            Text(
                                text = line,
                                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp),
                            )
                        }
                    }
                }
            }
        },
    )
}

private fun backActions(showConfidence: MutableState<Boolean>) {
    showConfidence.value = false
}

object PrivacyPolicyText {
    /**
     * Полный текст политики. Ссылки обработаны как Markdown-подобный синтаксис.
     * Для кликабельных ссылок в Compose нужно обработать [текст] и [ссылка].
     */
    const val FULL_TEXT = """
**Политика конфиденциальности приложения «КонтекстФото»**

**1. Основные понятия**

1.1. Приложение – мобильное приложение «КонтекстФото», представляющее собой галерею для просмотра фотографий с функцией добавления комментариев.

1.2. Разработчик – физическое лицо, создавшее Приложение и обеспечивающее его функционирование. Информация о Разработчике и контактные данные указаны в соответствующем разделе Приложения.

1.3. Пользователь – любое лицо, установившее и/или использующее Приложение.

1.4. Политика – настоящая Политика конфиденциальности приложения «КонтекстФото».


**2. Общие положения**

2.1. Настоящая Политика определяет порядок обработки и защиты информации о Пользователях, использующих Приложение.

2.2. Целью настоящей Политики является обеспечение надлежащей защиты информации о Пользователях, в том числе их персональных данных, от несанкционированного доступа и разглашения.

2.3. Отношения, связанные со сбором, хранением, распространением и защитой информации о Пользователях, регулируются настоящей Политикой и действующим российским законодательством.

2.4. Действующая редакция Политики доступна Пользователям в соответствующем разделе Приложения. Разработчик вправе вносить изменения в настоящую Политику. При внесении изменений новая редакция Политики размещается в Приложении. Продолжение использования Приложения Пользователем после внесения изменений означает согласие Пользователя с такими изменениями.

2.5. Используя Приложение, Пользователь выражает своё согласие с условиями настоящей Политики.

2.6. В случае несогласия Пользователя с условиями настоящей Политики использование Приложения должно быть немедленно прекращено.


**3. Условия использования Приложения**

3.1. Используя Приложение, Пользователь:
3.1.1. Подтверждает, что обладает всеми необходимыми правами для его использования;
3.1.2. Подтверждает, что указывает достоверную информацию о себе при регистрации, в объёмах, необходимых для использования Приложения;
3.1.3. Ознакомлен с настоящей Политикой и выражает своё согласие с ней.

3.2. Разработчик не проверяет достоверность получаемой информации о Пользователях, за исключением случаев, когда такая проверка необходима для исполнения обязательств перед Пользователем.


**4. Цели обработки информации**

Разработчик осуществляет обработку информации о Пользователях, в том числе их персональных данных, в следующих целях:
4.1. Идентификация Пользователя при регистрации и входе в учётную запись;
4.2. Синхронизация комментариев между устройствами Пользователя при использовании облачного хранилища;
4.3. Техническая поддержка и обработка обращений Пользователя;
4.4. Улучшение качества и оптимизация работы Приложения.


**5. Состав информации о Пользователях**

Разработчик может обрабатывать следующую информацию о Пользователях:
5.1. Данные, предоставляемые Пользователем при регистрации: адрес электронной почты и пароль.
5.2. Информация, предоставляемая Пользователями при использовании Приложения: тексты комментариев, добавляемые к фотографиям.
5.3. Информация, получаемая в процессе использования Приложения: данные о версии ОС и модели устройства.


**6. Обработка персональных данных Пользователей**

6.1. Обработка персональных данных осуществляется на основе принципов законности, добросовестности и соответствия целям обработки.

6.1.1. Условия и цели обработки персональных и иных данных
Разработчик осуществляет обработку персональных и иных данных Пользователя с его согласия, предусмотренного п. 6.1.2 настоящей Политики, в целях оказания услуг Пользователю по использованию Приложения.

6.1.2. Сбор персональных и иных данных
Сбор данных, предусмотренных пунктами 5.1 и 5.2 настоящей Политики, осуществляется при регистрации или в процессе использования Приложения. Согласие на обработку предоставляется Пользователем в форме конклюдентного действия при создании аккаунта внутри Приложения при нажатии соответствующей кнопки.

6.1.3. Передача персональных данных
Персональные данные Пользователей не передаются каким-либо третьим лицам, за исключением случаев использования облачного сервиса Firestore (Google) для хранения комментариев. В этом случае передача данных регулируется [Политикой конфиденциальности Google](https://policies.google.com/privacy), и Пользователь явно выбирает такой способ хранения. Также возможна передача данных по запросу государственных органов в порядке, предусмотренном законодательством РФ.

6.1.4. Хранение персональных данных
Данные, хранящиеся локально (комментарии на устройстве), не передаются Разработчику. Данные, хранящиеся в облаке (Firestore), могут храниться на серверах, расположенных в том числе за пределами РФ, но с соблюдением стандартов безопасности, применяемых Google.
Хранение персональных и иных данных Пользователя осуществляется в течение использования Приложения Пользователем, а после прекращения использования – до момента удаления учётной записи Пользователем либо в течение срока, необходимого для выполнения требований законодательства.

6.1.5. Прекращение обработки персональных данных
Обработка персональных данных Пользователя прекращается при достижении целей обработки, а именно в случаях:
    • удаления Пользователем своей учётной записи в Приложении (с использованием соответствующего функционала в настройках);
    • отзыва Пользователем согласия на обработку персональных данных путём обращения к Разработчику в порядке, предусмотренном разделом 10 Политики.


**7. Права и обязанности Пользователей**

7.1. Пользователи вправе:
7.1.1. Получать доступ к информации о себе путём обращения к Разработчику;
7.1.2. Требовать уточнения, блокирования или уничтожения своих персональных данных, если они являются неполными, устаревшими, недостоверными или если обработка осуществляется с нарушением законодательства;
7.1.3. На основании запроса получать от Разработчика информацию, касающуюся обработки их персональных данных;


**8. Меры по защите информации о Пользователях**

Разработчик принимает необходимые технические и организационные меры для обеспечения защиты персональных данных Пользователя от неправомерного или случайного доступа, уничтожения, изменения, блокирования, копирования, распространения, а также от иных неправомерных действий. К таким мерам относится шифрование канала передачи данных, хеширование паролей и ограничение доступа к данным.


**9. Ограничение действий Политики**

Действие настоящей Политики не распространяется на действия и интернет-ресурсы третьих лиц, на которые могут вести ссылки из Приложения, или на сервисы, используемые для хранения данных (Firestore). Разработчик не несёт ответственности за действия этих третьих лиц.


**10. Обращения Пользователей**

10.1. Пользователи вправе направлять Разработчику свои запросы, в том числе запросы относительно использования их персональных данных, предусмотренные разделом 7 настоящей Политики, в письменной форме или в форме электронного документа по адресу электронной почты, указанному в соответствующем разделе Приложения.

10.2. Запрос, направляемый Пользователем, должен содержать следующую информацию:
10.2.1. Адрес электронной почты, указанный при регистрации в Приложении;
10.2.2. Сведения, подтверждающие участие Пользователя в отношениях с Разработчиком (например, скриншот экрана профиля или детальное описание сути запроса);
10.2.3. Суть запроса.

10.3. Разработчик обязуется рассмотреть и направить ответ на поступивший запрос Пользователя в течение 30 дней с момента поступления обращения.

10.4. Контактный адрес Разработчика для связи и направления обращений: **cripton2020@mail.ru**. Персональные данные и иная информация о Пользователе, направившем запрос, не могут быть без специального согласия Пользователя использованы иначе, как для ответа по теме полученного запроса или в случаях, прямо предусмотренных законодательством.
"""
}
