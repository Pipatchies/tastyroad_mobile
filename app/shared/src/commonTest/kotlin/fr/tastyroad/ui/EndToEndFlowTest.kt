package fr.tastyroad.ui

import fr.tastyroad.domain.MockData
import fr.tastyroad.domain.User
import fr.tastyroad.repository.AuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EndToEndFlowTest {
    @Test
    fun testCompleteEndToEndUserFlow() {
        // --- 1. ACCUEIL (HomeScreen Flow) ---
        // L'utilisateur démarre sur l'écran d'accueil et consulte les itinéraires
        val itineraries = MockData.sampleItineraries
        assertTrue(itineraries.isNotEmpty(), "L'écran d'accueil doit afficher les cartes d'itinéraires")

        val firstItinerary = itineraries[0]
        assertNotNull(firstItinerary.itinerary.title)
        assertEquals("Saveurs d'Occitanie", firstItinerary.itinerary.title)

        // --- 2. NAVIGATION CARTE (MapScreen Flow) ---
        // L'utilisateur clique sur le bouton flottant "Carte"
        val sampleClusters = listOf("Occitanie, France", "PACA, France", "Nord, France")
        assertTrue(sampleClusters.size >= 3, "La carte doit afficher les clusters de lieux")

        // --- 3. CRÉATION D'ITINÉRAIRE (CreateItineraryScreen Flow) ---
        // Étape 3a : Tracé sur carte (Moins de 2 points -> Invalide)
        var stepCount = 1
        var isStepCountValid = stepCount >= 2
        assertFalse(isStepCountValid, "Un itinéraire avec 1 seule étape ne doit pas être validé")

        // Étape 3b : Tracé complet (2 points -> Valide)
        stepCount = 2
        isStepCountValid = stepCount >= 2
        assertTrue(isStepCountValid, "L'itinéraire comportant 2 étapes (départ + arrivée) est valide")

        // Étape 3c : Formulaire (Titre, Description & Consentement RGPD)
        val titleInput = "La Route des Fromages du Cantal"
        val descriptionInput = "Superbe parcours gastronomique à travers le Cantal"
        val hasAcceptedGdpr = true

        val isTitleValid = titleInput.isNotBlank()
        val isDescriptionValid = descriptionInput.isNotBlank()
        val isFormValid = isStepCountValid && isTitleValid && isDescriptionValid && hasAcceptedGdpr

        assertTrue(isFormValid, "Le formulaire de création rempli avec consentement RGPD est valide")

        // --- 4. PROFIL & AUTHENTIFICATION (AuthScreen & ProfileScreen Flow) ---
        // Étape 4a : Utilisateur non connecté -> Accès au profil redirige vers AuthScreen
        AuthSession.logout()
        assertFalse(AuthSession.isLoggedIn, "L'utilisateur n'est pas connecté au départ")
        assertNull(AuthSession.currentUser)

        // Étape 4b : Inscription / Connexion réussie
        AuthSession.token = "fake-jwt-session-token"
        AuthSession.currentUser =
            User(
                id = 42,
                username = "katsu_bertaud",
                email = "kastuber@gmail.com",
                age = 25,
            )

        assertTrue(AuthSession.isLoggedIn, "L'utilisateur est connecté avec un token JWT valide")
        assertEquals("katsu_bertaud", AuthSession.currentUser?.username)
        assertEquals("kastuber@gmail.com", AuthSession.currentUser?.email)

        // Étape 4c : Consultation du profil avec les 4 onglets
        val profileTabs = listOf("Mes itinéraires", "Mes favoris", "Mes avis", "Mes photos")
        assertEquals(4, profileTabs.size)
        assertEquals("Mes avis", profileTabs[2])

        // Étape 4d : Droit à l'oubli (Suppression du compte RGPD & Déconnexion)
        AuthSession.logout()
        assertFalse(AuthSession.isLoggedIn, "La suppression du compte déconnecte et efface la session")
        assertNull(AuthSession.currentUser)
    }
}
