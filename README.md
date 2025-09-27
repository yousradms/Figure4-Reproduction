# Reproduction de la Figure 4 : Injecting Shortcuts for Faster Running Java Code

Ce projet reproduit la Figure 4 de l'article [Injecting Shortcuts for Faster Running of Java Code](https://www.maths.stir.ac.uk/~sbr/files/InjectingShortcutsCEC2020_cameraReady.pdf).  
L'objectif est de simuler l'injection d'instructions de contrôle de flux (`break`, `continue`, `return`, avec ou sans condition `if`) dans trois projets Java (**jCodec**, **spark**, **spatial4j**) et de générer des graphiques en barres montrant les taux de succès de compilation.

## Aperçu

Le projet utilise :

- **JavaParser** (version 3.25.10) pour valider la syntaxe des fragments de code générés.  
- **JFreeChart** (version 1.5.4) pour créer des graphiques en barres.  
- Une simulation basée sur des probabilités estimées à partir de la Figure 4, avec une **graine aléatoire fixe (42)** pour garantir la reproductibilité.  
- **Maven** pour gérer les dépendances.  

## Résultats

Les résultats sont des graphiques en barres générés dans le dossier output/ :
- jCodec_repro.png
- spark_repro.png 
- spatial4j_repro.png 

Un rapport LaTeX détaillant l'expérience est également fourni.
