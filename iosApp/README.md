# Running the iOS demo

If you are running the iOS app locally, provide your Apple Team ID in an
`ExternalDeveloper.xcconfig` file so local signing settings stay out of git:

1. Run
   `cp Configuration/ExternalDeveloper.xcconfig.example Configuration/ExternalDeveloper.xcconfig`
2. Edit `Configuration/ExternalDeveloper.xcconfig` and set `TEAM_ID=YOUR_APPLE_TEAM_ID`
3. Clean and rebuild the iOS app in Xcode
