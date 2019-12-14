package grammar

data class Header(val assumptions: List<Node>, val expression: Node)

fun getHeader(header: String, div: String = "|-"): Header {
    val assumptionsEnd = header.indexOf(div)
    return Header(
            if (assumptionsEnd < 1)
                listOf()
            else
                header
                        .substring(0, assumptionsEnd)
                        .split(',')
                        .map(String::parse),
            header.substring(assumptionsEnd + 2, header.length).parse()
    )
}