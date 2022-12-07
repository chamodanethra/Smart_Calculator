package calculator

import java.math.BigInteger

fun main() {
    val variables = mutableMapOf<String, BigInteger>()
    while (true) {
        val s = readln()
        if (s.isEmpty()) continue
        try {
            if (s.contains('=') || s.startsWith('/')) {
                when {
                    s == "/help" -> println("The program calculates the answer of a complex mathematical expression")
                    s == "/exit" -> println("Bye!").also { return }
                    s.startsWith("/") -> println("Unknown command")
                    s.split('=').size > 2 -> throw NumberFormatException()
                    else -> {
                        if (s.substringBefore('=').trim()
                                .contains("[^a-zA-Z]".toRegex())
                        ) println("Invalid identifier").also { return@also }
                        variables[s.substringBefore('=').trim()] =
                            simplifyExpression(s.substringAfter('=').trim(), variables.toMap())
                    }
                }
            } else println(simplifyExpression(s.trim(), variables.toMap()))
        } catch (e: NumberFormatException) {
            println("Invalid assignment")
        } catch (e: NullPointerException) {
            println("Unknown variable")
        } catch (e: Exception) {
            println("Invalid expression")
        }
    }
}

private fun simplifyExpression(exp: String, variables: Map<String, BigInteger>): BigInteger {
    var lastLeftParenthesisIndex = -1
    var modExp = exp
    if (modExp.count { it == '(' } != modExp.count { it == ')' }) throw Exception()
    if (modExp.contains("""[\\*/]{2,}""".toRegex())) throw Exception()
    for (i in modExp.indices) {
        if (modExp[i] == '(') {
            lastLeftParenthesisIndex = i
        } else if (modExp[i] == ')') {
            if (lastLeftParenthesisIndex == -1) throw Exception()
            val subResult = evaluateExpression(exp.substring(lastLeftParenthesisIndex + 1 until i), variables)
            modExp = modExp.replace(exp.substring(lastLeftParenthesisIndex, i + 1), subResult.toString())
            break
        }
    }
    if (modExp.contains("(")) {
        modExp = simplifyExpression(modExp, variables).toString()
    }
    return evaluateExpression(modExp, variables)
}

private fun replaceMatch(exp: String, variables: Map<String, BigInteger>, matchExp: Regex): String {
    var modExp = exp
    while (true) {
        val match = matchExp.matchEntire(modExp) ?: break
        val (_, left, right) = match.groupValues
        val leftOperand = left.substringAfterLast('.')
        val rightOperand = right.substringBefore('.')
        var leftValue = if (leftOperand.contains("\\d+".toRegex())) leftOperand.toBigInteger()
        else (variables[leftOperand.substringAfter('-')]!!
                * if (leftOperand.first() == '-') BigInteger.valueOf(-1) else BigInteger.valueOf(1))
        var rightValue = if (rightOperand.contains("\\d+".toRegex())) rightOperand.toBigInteger()
        else (variables[rightOperand.substringAfter('-')]!! * if (rightOperand.first() == '-') BigInteger.valueOf(-1) else BigInteger.valueOf(
            1
        ))
        val subResult = if (exp.contains("*")) leftValue * rightValue else leftValue / rightValue
        val operation = if (exp.contains("*")) "\\*" else "/"
        modExp = modExp.replace("""$leftOperand *$operation * *$rightOperand""".toRegex(), subResult.toString())
    }
    return modExp
}

private fun evaluateExpression(exp: String, variables: Map<String, BigInteger>): BigInteger {
    var modExp = ".".plus(exp.replace(" *(\\+|-{2})+ *".toRegex(), ".").replace(""" *\.?-+ *""".toRegex(), ".-"))
    val matchExp1 = """.*\.(-?\w+) *\* *(-?\w+).*""".toRegex()
    val matchExp2 = """.*\.(-?\w+) */ *(-?\w+).*""".toRegex()
    modExp = replaceMatch(modExp, variables, matchExp1)
    modExp = replaceMatch(modExp, variables, matchExp2)
    println(modExp)
    return modExp.trimStart('.').split(".").sumOf {
        if (it in listOf("-", "")) throw Exception()
        if (it.contains("\\d+".toRegex())) it.toBigInteger()
        else variables[it.substringAfter('-')]!! * if (it.first() == '-') BigInteger.valueOf(-1) else BigInteger.valueOf(
            1
        )
    }
}