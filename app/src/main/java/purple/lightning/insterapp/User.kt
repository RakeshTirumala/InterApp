package purple.lightning.insterapp

data class User(
    var username: String? = null,
    var password: String?= null,
    var followers: MutableMap<String, String> = mutableMapOf(),
    var following: MutableMap<String, String> = mutableMapOf(),
    var posts: MutableMap<String, String> = mutableMapOf(),
    var isPrivate:Boolean = false,
    var email:String = ""
)
