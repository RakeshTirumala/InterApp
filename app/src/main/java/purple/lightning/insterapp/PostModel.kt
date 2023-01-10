package purple.lightning.insterapp

data class PostModel(
    var username:String = "",
    var captionMap: MutableMap<String, String>,
    var commentsMap: MutableMap<String, List<UserPostResponseModel>>?= null,
    var likesMap: MutableMap<String, List<UserPostResponseModel>>?=null
)
