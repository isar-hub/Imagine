    package com.isar.imagine.data
    typealias Root = List<Root2>;
    data class Root2(
        val uid: String,
        val email: String,
        val emailVerified: Boolean,
        val displayName: String,
        val disabled: Boolean,
        val metadata: Metadata,
        val passwordHash: String,
        val passwordSalt: String,
        val tokensValidAfterTime: String,
        val providerData: List<ProviderDaum>,
    )

    data class Metadata(
        val lastSignInTime: String,
        val creationTime: String,
        val lastRefreshTime: String,
    )

    data class ProviderDaum(
        val uid: String,
        val displayName: String,
        val email: String,
        val providerId: String,
    )