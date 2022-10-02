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
    <string name="mainWelcomeYear" translatable="false">2021</string>
    ```

2. Update app/build.gradle:

    ```text
    versionName "x.y.z" // remove "-dev" suffix
    versionCode +1
    ```

    - [About versioning](https://developer.android.com/studio/publish/versioning): "Typically, you would release the first version of your app with `versionCode` set to 1, then monotonically increase the value with each release, regardless of whether the release constitutes a major or minor release"

3. Tag last commit "vx.y.z" and push. This will trigger the release github action.

4. Update created release with changes.

5. [Edit gh-pages](https://github.com/phramusca/JaMuz/edit/gh-pages/index.md) with link to new release.

6. Update app/build.gradle

    ```text
    versionName "x.y.z+1-dev" 
    ```

7. Commit (named vx.y.z+1-dev) & push.

8. Update [voiceCommands.md](https://github.com/phramusca/JaMuz-Remote/blob/master/data/voiceCommands.md):

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
