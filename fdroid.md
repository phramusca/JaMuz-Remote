# F-Droid

Ce document décrit le processus F-Droid pour JaMuz-Remote et comment faire passer le build en local.

*Références : [Documentation F-Droid](https://f-droid.org/docs/) (Building Applications, Build Metadata Reference), [MR !11561](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/11561) (inclusion initiale).*

---

## Processus et conseils

- **Process** : Fork [fdroiddata](https://gitlab.com/fdroid/fdroiddata) (GitLab), éditer `metadata/org.phramusca.jamuz.yml`, ouvrir une MR avec le template « App update ». Les mainteneurs peuvent demander de corriger le `versionCode` dans les métadonnées ou de rebaser sur `upstream/master`.
- **versionCode** : Chaque entrée de build dans le YAML doit avoir un `versionCode` qui correspond à l’app au `commit`/tag donné. `CurrentVersion` et `CurrentVersionCode` doivent refléter la dernière version publiée. Si le build F-Droid échoue pour « versionCode », ajouter ou corriger l’entrée de build et mettre à jour `CurrentVersion` / `CurrentVersionCode`.
- **Rebase (fdroiddata)** — avec **origin** = ton fork (ex. `phramusca/fdroiddata`), **upstream** = officiel (`fdroid/fdroiddata`) :
  1. `git fetch upstream`
  2. `git checkout org.phramusca.jamuz`
  3. `git rebase upstream/master`
  4. Résoudre les conflits si besoin (souvent dans `metadata/org.phramusca.jamuz.yml`), puis `git rebase --continue`
  5. `git push --force-with-lease origin org.phramusca.jamuz`
  Si la [MR du bot checkupdates](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/34785) est bloquée par « Fast forward merge is not possible. Please rebase », faire la même mise à jour depuis ton fork (rebase sur `upstream/master` et ouvrir une MR depuis ta branche vers `fdroid/fdroiddata` master).
- **Build from source** : Les JAR et libs natives doivent être buildables depuis les sources ou provenir d’un dépôt de confiance (pas de binaires opaques dans le dépôt). Voir les commentaires de la MR si le scanner signale des problèmes.

---

## Build en local

Pour vérifier que F-Droid peut builder l’app (même flux que [Building Applications](https://f-droid.org/docs/Building_Applications/)).

### 1. Prérequis

- **fdroidserver** : installé (ex. `pipx run fdroidserver` ou env dédié).
- **Android SDK** : installé (ex. `~/Android/Sdk`). À exposer via **`ANDROID_HOME`** (pas besoin de `sdk_path` dans `config.yml`).
- **Java 17** : le projet est en Java 17 ; utiliser **`JAVA_HOME`** pointant vers un JDK 17.
- **gradlew-fdroid** : le paquet pip/pipx ne fournit pas toujours le script. Option A : l’indiquer dans `config.yml` avec `gradle: "/chemin/vers/gradlew-fdroid"`. Option B : installer le script (voir ci‑dessous) et l’avoir dans le PATH ou dans `config.yml`.

### 2. Script gradlew-fdroid (si besoin)

Si `fdroid build` échoue avec « Exec format error » sur `gradlew-fdroid`, le binaire est souvent du HTML (page 404) au lieu du script bash. Récupérer le vrai script (ex. [sources Debian](https://salsa.debian.org/android-tools-team/gradlew-fdroid)) et le placer ex. dans `~/.local/bin/gradlew-fdroid` avec `chmod +x`.

**Gradle 9.3.1** : les anciennes versions du script n’incluent pas le hash pour Gradle 9.3.1. Soit utiliser une version du script qui s’appuie sur le [transparency log](https://gradle.org/transparency-log/) (ex. [fdroid/gradlew-fdroid](https://gitlab.com/fdroid/gradlew-fdroid)), soit ajouter manuellement dans le script l’entrée pour 9.3.1 (case `get_sha` + liste `plugin_v`) avec le SHA-256 de `gradle-9.3.1-bin.zip` depuis le transparency log.

### 3. Config fdroiddata

- Cloner le dépôt de données (ton fork ou `git clone https://gitlab.com/fdroid/fdroiddata.git`) et `cd fdroiddata`.
- Copier `config.yml` depuis [fdroidserver](https://gitlab.com/fdroid/fdroidserver) (`examples/config.yml`) et l’adapter : utiliser des **chemins en chaînes** pour `gpghome`, `keystore`, `serverwebroot` (pas `{env: ...}`), sinon fdroid peut planter dans `fill_config_defaults`. Optionnel : `mkdir -p /tmp/repo/status` si `serverwebroot: "/tmp"` pour éviter les erreurs rsync.
- Ne pas mettre `sdk_path` dans `config.yml` : lancer le build avec **`ANDROID_HOME`** (et **`JAVA_HOME`**) dans l’environnement.

### 4. Lancer le build avec les variables d’environnement

Depuis le répertoire **fdroiddata** :

```bash
export ANDROID_HOME="$HOME/Android/Sdk"   # ou le chemin de ton SDK
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64   # adapter à ton système

fdroid build org.phramusca.jamuz:20
```

Une seule version (ex. `:20`) : `fdroid build org.phramusca.jamuz:20`. Les APK non signés vont dans `unsigned/` ; `fdroid build --help` pour les options.

### 5. Problèmes courants

- **« Unsupported class file major version 65 »** : Gradle tourne avec une Java trop récente pour l’ancien bytecode. Utiliser Java 17 : `JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 fdroid build ...`
- **« invalid source release: 21 »** : le tag/clone demande Java 21 alors que le build utilise Java 17. Le dépôt est en Java 17 ; pour la version 0.6.13 (tag v0.6.13 avec encore Java 21), les métadonnées F-Droid utilisent un patch `java17.patch` dans `metadata/org.phramusca.jamuz/` pour forcer Java 17 au build. Les releases suivantes (déjà en Java 17) n’en ont pas besoin.
- **« SDK location not found »** : définir `ANDROID_HOME` (pointant vers le répertoire du SDK Android) avant d’exécuter `fdroid build`.
- **« Unexpected version/version code in output »** : le `versionName` dans l’APK (ex. `0.6.14-dev`) ne correspond pas à celui attendu par les métadonnées (ex. `0.6.13`). Vérifier que le build cible le bon commit/tag (ex. tag `v0.6.13` pour la version 0.6.13).

### 6. Résolutions passées (MR !11561 et suite)

- **JAR dans `app/libs`** : le scanner exige des dépendances buildables ou depuis Maven. Les JAR locaux ont été retirés et remplacés par des dépendances Maven (ex. `net.jthink:jaudiotagger`).
- **versionCode** : les entrées de build dans les métadonnées doivent correspondre au `versionCode` de l’app au commit donné ; corrections faites dans le YAML (et `subdir: app`, `gradle: yes`).
- **Gradle wrapper** : incohérence de version et `distributionSha256Sum` manquant ; mise à jour du wrapper et ajout de `distributionSha256Sum` pour reproductibilité.
- **OutOfMemoryError (CI)** : si le CI F-Droid échoue avec `D8: java.lang.OutOfMemoryError: Java heap space`, garder dans `gradle.properties` une ligne du type `org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8`. **Pas obligatoire pour un build local** (souvent assez de RAM), mais **recommandé pour le CI** et les environnements limités.

---

## Option release (rappel)

Dans le process de release du projet, F-Droid est optionnel : avec `UpdateCheckMode: Tags` et `AutoUpdateMode: Version`, les nouveaux tags sont détectés automatiquement. Si tu modifies les métadonnées (nouvelle app ou correction), utiliser le dépôt [fdroiddata](https://gitlab.com/fdroid/fdroiddata) : fork → branche → éditer `metadata/org.phramusca.jamuz.yml` → MR avec le template « App update ».
