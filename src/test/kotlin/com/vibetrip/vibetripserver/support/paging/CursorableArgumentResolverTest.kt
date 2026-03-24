package com.vibetrip.vibetripserver.support.paging

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest

class CursorableArgumentResolverTest :
    BehaviorSpec(
        {
            val resolver = CursorableArgumentResolver()
            val webRequest = mockk<NativeWebRequest>()
            val binderFactory = mockk<WebDataBinderFactory>()

            Given("supportsParameter 검증 시") {
                When("파라미터 타입이 Cursorable이면") {
                    val parameter = mockk<MethodParameter>()
                    every { parameter.parameterType } returns Cursorable::class.java

                    Then("true를 반환한다") {
                        resolver.supportsParameter(parameter) shouldBe true
                    }
                }

                When("파라미터 타입이 Cursorable이 아니면") {
                    val parameter = mockk<MethodParameter>()
                    every { parameter.parameterType } returns String::class.java

                    Then("false를 반환한다") {
                        resolver.supportsParameter(parameter) shouldBe false
                    }
                }
            }

            Given("@CursorDefault 어노테이션이 있는 상황에서") {
                val parameter = mockk<MethodParameter>()
                val annotation = CursorDefault(defaultLimit = 20)

                every { parameter.getParameterAnnotation(CursorDefault::class.java) } returns annotation

                When("limit 파라미터가 있으면") {
                    every { webRequest.getParameter("limit") } returns "15"
                    every { webRequest.getParameter("cursor") } returns null

                    Then("요청한 limit 값이 적용된다") {
                        val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

                        result shouldBe Cursorable<Long>(null, 15)
                    }
                }

                When("limit 파라미터가 없으면") {
                    every { webRequest.getParameter("limit") } returns null
                    every { webRequest.getParameter("cursor") } returns null

                    Then("기본값이 적용된다") {
                        val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

                        result shouldBe Cursorable<Long>(null, 20)
                    }
                }

                When("limit 파라미터가 숫자가 아니면") {
                    every { webRequest.getParameter("limit") } returns "invalid"
                    every { webRequest.getParameter("cursor") } returns null

                    Then("기본값이 적용된다") {
                        val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

                        result shouldBe Cursorable<Long>(null, 20)
                    }
                }
            }

            Given("@CursorDefault 어노테이션이 없는 상황에서") {
                val parameter = mockk<MethodParameter>()

                every { parameter.getParameterAnnotation(CursorDefault::class.java) } returns null

                When("limit 파라미터가 있으면") {
                    every { webRequest.getParameter("limit") } returns "10"
                    every { webRequest.getParameter("cursor") } returns null

                    Then("요청한 limit 값이 적용된다") {
                        val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

                        result shouldBe Cursorable<Long>(null, 10)
                    }
                }

                When("limit 파라미터가 없으면") {
                    every { webRequest.getParameter("limit") } returns null

                    Then("INVALID_PAGING_PARAMETER 예외가 발생한다") {
                        val exception =
                            shouldThrow<AppException> {
                                resolver.resolveArgument(parameter, null, webRequest, binderFactory)
                            }

                        exception.errorType shouldBe ErrorType.INVALID_PAGING_PARAMETER
                    }
                }

                When("limit 파라미터가 숫자가 아니면") {
                    every { webRequest.getParameter("limit") } returns "abc"

                    Then("INVALID_PAGING_PARAMETER 예외가 발생한다") {
                        val exception =
                            shouldThrow<AppException> {
                                resolver.resolveArgument(parameter, null, webRequest, binderFactory)
                            }

                        exception.errorType shouldBe ErrorType.INVALID_PAGING_PARAMETER
                    }
                }
            }

            Given("limit 범위 검증 시") {
                val parameter = mockk<MethodParameter>()

                every { parameter.getParameterAnnotation(CursorDefault::class.java) } returns null
                every { webRequest.getParameter("cursor") } returns null

                When("limit이 0이면") {
                    every { webRequest.getParameter("limit") } returns "0"

                    Then("INVALID_PAGING_SIZE 예외가 발생한다") {
                        val exception =
                            shouldThrow<AppException> {
                                resolver.resolveArgument(parameter, null, webRequest, binderFactory)
                            }

                        exception.errorType shouldBe ErrorType.INVALID_PAGING_SIZE
                    }
                }

                When("limit이 50을 초과하면") {
                    every { webRequest.getParameter("limit") } returns "51"

                    Then("INVALID_PAGING_SIZE 예외가 발생한다") {
                        val exception =
                            shouldThrow<AppException> {
                                resolver.resolveArgument(parameter, null, webRequest, binderFactory)
                            }

                        exception.errorType shouldBe ErrorType.INVALID_PAGING_SIZE
                    }
                }

                When("limit이 1이면") {
                    every { webRequest.getParameter("limit") } returns "1"

                    Then("정상적으로 처리된다") {
                        val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

                        result shouldBe Cursorable<Long>(null, 1)
                    }
                }

                When("limit이 50이면") {
                    every { webRequest.getParameter("limit") } returns "50"

                    Then("정상적으로 처리된다") {
                        val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

                        result shouldBe Cursorable<Long>(null, 50)
                    }
                }
            }
        },
    )
