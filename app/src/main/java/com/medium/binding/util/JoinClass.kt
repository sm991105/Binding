package com.medium.binding.util

import java.util.regex.Pattern

object JoinClass {

    // 한글(자음+모음+공백+언더바+숫자)만 허용하는 정규표현식, 2~8자
    // private val regExp =  "^(?=.*[ㄱ-ㅎ|ㅏ-ㅣ|가-힣\\s_0-9]).{2,8}$"

    // 한글(자음+모음)만 허용하는 식, 2~8자
    // private val regExp = "^(?=.*[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]).{2,8}$"

    // 닉네임 정규표현식
    private const val nicknameRegExp = "^(?=.*[가-힣]).{2,8}$"

    // 정규식으로 한글 닉네임 형식 검사
    fun isValidNickname(nickname: String):Boolean {
        var returnValue = false

        try
        {
            val p = Pattern.compile(nicknameRegExp)
            val m = p.matcher(nickname)

            if (m.matches()) {
                returnValue = true
            }

            return returnValue
        }
        catch (e: Exception) {
            return false
        }
    }
}