fun main() {
    val a = readln().toBigInteger()
    val b = readln().toBigInteger()
    println((a+b + (a-b).abs())/2.toBigInteger())
}