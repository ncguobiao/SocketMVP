package com.gb.socket1

import org.junit.Test

import java.util.ArrayList

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        val list = ArrayList<String>()
        list.add("haha")
        list.add("aaaa")
        list.add("cccc")

        list.removeAt(1)
        list.forEach {
            print(it)
        }
    }
}