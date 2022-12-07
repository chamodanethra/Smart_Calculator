import java.math.BigInteger

fun main() {
    val a = readln().toBigInteger()
    val b = readln().toBigInteger()
    val gcdValue = gcd(if (a >b) a else b, if (a > b) b else a)
    println(a * b / gcdValue)
}

fun gcd(a: BigInteger, b: BigInteger): BigInteger {
    if (b==0.toBigInteger()) return a
    return gcd(b, a % b)
}