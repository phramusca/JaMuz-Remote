# Contributing

By contributing to this project you agree to license your contribution under the terms of the [GNU GPLv3](LICENSE).

## Issues

[Open an issue](https://github.com/phramusca/JaMuz-Remote/issues?state=open) for anything you would like to see in JaMuz, but please check other issues first.

## Internationalization

Using [weblate.org](https://hosted.weblate.org/engage/jamuz-remote/).

## Pull Requests

Pull requests are welcome.
Please submit to the `master` branch.

### Get Started

- Clone repository.
- Open project using [Android Studio](https://developer.android.com/studio/).
- You can now run and enjoy (hopefully).

## Release process

1. Update year in strings.xml:  

    ```xml
    <string name="mainWelcomeYear" translatable="false">2022</string>
    ```

1. Update app/build.gradle:

    ```text
    versionName "x.y.z" // remove "-dev" suffix
    versionCode +1
    ```

    - [About versioning](https://developer.android.com/studio/publish/versioning): "Typically, you would release the first version of your app with `versionCode` set to 1, then monotonically increase the value with each release, regardless of whether the release constitutes a major or minor release"

1. Create metadata/{language}/changelogs/{versionCode}.txt and list changes.

    - {language}: https://en.wikipedia.org/wiki/IETF_language_tag

1. Tag last commit "vx.y.z" and push. This will trigger the [release github action](https://github.com/phramusca/JaMuz-Remote/actions/workflows/release.yml).

1. Check [created release](https://github.com/phramusca/JaMuz-Remote/releases).  
   If the workflow failed at "Create release" (e.g. "Cannot upload assets to an immutable release"), you must restart the release process: release assets are immutable, so you cannot reuse the same tag even after deleting the tag and the release.

1. **Optionally** F-Droid
    - With `UpdateCheckMode: Tags` and `AutoUpdateMode: Version`, new tags are picked up automatically.
    - To change metadata: [fdroiddata](https://gitlab.com/fdroid/fdroiddata) (GitLab) → fork → edit `metadata/org.phramusca.jamuz.yml` → MR with template **"App update"**. See [F-Droid docs](https://f-droid.org/docs/) and [MR !11561](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/11561).
    - **Full process, local build, troubleshooting**: [fdroid.md](fdroid.md).


1. **Optionally** [Edit gh-pages](https://github.com/phramusca/JaMuz/edit/gh-pages/index.md) — links there already point to the latest release.

1. Update app/build.gradle

    ```text
    versionName "x.y.z+1-dev" 
    ```

1. Commit (named vx.y.z+1-dev) & push.

1. Update [voiceCommands.md](https://github.com/phramusca/JaMuz-Remote/blob/master/data/voiceCommands.md):

    - If any of the following changed :
        - in `VoiceKeyWords.java`,
        - or in `res/values/strings.xml` files :

    ```xml
    <string-array name="voiceCommands_XXXXXX">
    </string-array>
    ```

    - Then, [export vocal commands](../../ReposSides/JaMuz-Remote/VoiceKeyWordsExport) to [voiceCommands.md](https://github.com/phramusca/JaMuz-Remote/edit/master/data/voiceCommands.md)

    ```bash
    cd ../../ReposSides/JaMuz-Remote/VoiceKeyWordsExport/ && dotnet run Program.cs
    ```
