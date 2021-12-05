package com.example.criclivescore

class ListItem {
    var header: String
    var description: String
    var matchId: String
    var details: String

    constructor(header: String, description: String, matchId: String) {
        this.header = header
        this.description = description
        this.matchId = matchId
        details = ""
    }

    constructor(header: String, description: String, matchId: String, details: String) {
        this.header = header
        this.description = description
        this.matchId = matchId
        this.details = details
    }
}