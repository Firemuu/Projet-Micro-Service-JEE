# Projet-Micro-Service-JEE

## Protocole supervisé naïf

Chaque philosophe pense, devient affamé, interroge `SupervisorService` pour sa
fourchette gauche, puis interroge à nouveau le superviseur pour sa fourchette
droite. Le superviseur ne décide pas si le philosophe peut manger.

### Failles mises en évidence par les tests

- **Interblocage** : les cinq philosophes peuvent chacun prendre une fourchette
  gauche et attendre la droite, qui est alors indisponible.
- **Absence de cohérence globale** : le superviseur accepte une première
  fourchette, puis une seconde, sans vérifier qu'elles sont effectivement
  attribuables au philosophe ou si celui-ci est autorisé à manger.
- **Écrasement potentiel des ressources** : les méthodes sont séparées entre
  vérification et réservation (`isForkAvailable` puis `requestFork`), ce qui
  expose une course entre l'interrogation et la demande.
- **Libération incorrecte** : une autre identité peut demander la libération
  d'une fourchette détenue par un philosophe, car aucune authentification ni
  vérification du propriétaire n'est réalisée par la logique globale.
- **Famine possible** : aucun mécanisme d'équité ne garantit qu'un philosophe
  bloqué obtiendra bientôt ses deux fourchettes.

Les tests de défaut se trouvent dans
`src/test/java/com/test/demo/supervisor/SupervisorServiceTest.java`.