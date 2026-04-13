package com.vibetrip.vibetripserver.support.paging

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest

class CursorableArgumentResolverTest {
    private lateinit var resolver: CursorableArgumentResolver
    private lateinit var webRequest: NativeWebRequest
    private lateinit var binderFactory: WebDataBinderFactory

    @BeforeEach
    fun setUp() {
        resolver = CursorableArgumentResolver()
        webRequest = mockk()
        binderFactory = mockk()
    }

    @Nested
    inner class SupportsParameter {
        @Test
        fun `파라미터 타입이 Cursorable이면 true를 반환한다`() {
            // given
            val parameter = mockk<MethodParameter>()
            every { parameter.parameterType } returns Cursorable::class.java

            // when
            val result = resolver.supportsParameter(parameter)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `파라미터 타입이 Cursorable이 아니면 false를 반환한다`() {
            // given
            val parameter = mockk<MethodParameter>()
            every { parameter.parameterType } returns String::class.java

            // when
            val result = resolver.supportsParameter(parameter)

            // then
            assertThat(result).isFalse()
        }
    }

    @Nested
    inner class `CursorDefault 어노테이션이 있는 경우` {
        private lateinit var parameter: MethodParameter
        private lateinit var annotation: CursorDefault

        @BeforeEach
        fun setUp() {
            parameter = mockk()
            annotation = CursorDefault(defaultLimit = 20)
            every { parameter.getParameterAnnotation(CursorDefault::class.java) } returns annotation
        }

        @Test
        fun `limit 파라미터가 있으면 요청한 limit 값이 적용된다`() {
            // given
            every { webRequest.getParameter("limit") } returns "15"
            every { webRequest.getParameter("cursor") } returns null

            // when
            val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

            // then
            assertThat(result).isEqualTo(Cursorable<Long>(null, 15))
        }

        @Test
        fun `limit 파라미터가 없으면 기본값이 적용된다`() {
            // given
            every { webRequest.getParameter("limit") } returns null
            every { webRequest.getParameter("cursor") } returns null

            // when
            val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

            // then
            assertThat(result).isEqualTo(Cursorable<Long>(null, 20))
        }

        @Test
        fun `limit 파라미터가 숫자가 아니면 기본값이 적용된다`() {
            // given
            every { webRequest.getParameter("limit") } returns "invalid"
            every { webRequest.getParameter("cursor") } returns null

            // when
            val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

            // then
            assertThat(result).isEqualTo(Cursorable<Long>(null, 20))
        }
    }

    @Nested
    inner class `CursorDefault 어노테이션이 없는 경우` {
        private lateinit var parameter: MethodParameter

        @BeforeEach
        fun setUp() {
            parameter = mockk()
            every { parameter.getParameterAnnotation(CursorDefault::class.java) } returns null
        }

        @Test
        fun `limit 파라미터가 있으면 요청한 limit 값이 적용된다`() {
            // given
            every { webRequest.getParameter("limit") } returns "10"
            every { webRequest.getParameter("cursor") } returns null

            // when
            val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

            // then
            assertThat(result).isEqualTo(Cursorable<Long>(null, 10))
        }

        @Test
        fun `limit 파라미터가 없으면 INVALID_PAGING_PARAMETER 예외가 발생한다`() {
            // given
            every { webRequest.getParameter("limit") } returns null

            // when & then
            assertThatThrownBy {
                resolver.resolveArgument(parameter, null, webRequest, binderFactory)
            }.isInstanceOf(AppException::class.java)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_PAGING_PARAMETER)
        }

        @Test
        fun `limit 파라미터가 숫자가 아니면 INVALID_PAGING_PARAMETER 예외가 발생한다`() {
            // given
            every { webRequest.getParameter("limit") } returns "abc"

            // when & then
            assertThatThrownBy {
                resolver.resolveArgument(parameter, null, webRequest, binderFactory)
            }.isInstanceOf(AppException::class.java)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_PAGING_PARAMETER)
        }
    }

    @Nested
    inner class `limit 범위 검증` {
        private lateinit var parameter: MethodParameter

        @BeforeEach
        fun setUp() {
            parameter = mockk()
            every { parameter.getParameterAnnotation(CursorDefault::class.java) } returns null
            every { webRequest.getParameter("cursor") } returns null
        }

        @Test
        fun `limit이 0이면 INVALID_PAGING_SIZE 예외가 발생한다`() {
            // given
            every { webRequest.getParameter("limit") } returns "0"

            // when & then
            assertThatThrownBy {
                resolver.resolveArgument(parameter, null, webRequest, binderFactory)
            }.isInstanceOf(AppException::class.java)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_PAGING_SIZE)
        }

        @Test
        fun `limit이 50을 초과하면 INVALID_PAGING_SIZE 예외가 발생한다`() {
            // given
            every { webRequest.getParameter("limit") } returns "51"

            // when & then
            assertThatThrownBy {
                resolver.resolveArgument(parameter, null, webRequest, binderFactory)
            }.isInstanceOf(AppException::class.java)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_PAGING_SIZE)
        }

        @Test
        fun `limit이 1이면 정상적으로 처리된다`() {
            // given
            every { webRequest.getParameter("limit") } returns "1"

            // when
            val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

            // then
            assertThat(result).isEqualTo(Cursorable<Long>(null, 1))
        }

        @Test
        fun `limit이 50이면 정상적으로 처리된다`() {
            // given
            every { webRequest.getParameter("limit") } returns "50"

            // when
            val result = resolver.resolveArgument(parameter, null, webRequest, binderFactory)

            // then
            assertThat(result).isEqualTo(Cursorable<Long>(null, 50))
        }
    }
}
