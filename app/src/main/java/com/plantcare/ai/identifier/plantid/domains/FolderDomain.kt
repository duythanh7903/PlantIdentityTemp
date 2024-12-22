package com.plantcare.ai.identifier.plantid.domains

data class FolderDomain(
    var folderName: String,
    var images: MutableList<ImageDomain>,
) {
}