Module SpotifySend

    Function Main(ByVal cmdArgs() As String) As Integer
        If cmdArgs.Length > 0 Then
            Dim arg As String

            arg = cmdArgs(0)

            If arg = "PlayNext" Then
                spotify.PlayNext()
            ElseIf arg = "PlayPause" Then
                spotify.PlayPause()
            ElseIf arg = "PlayPrev" Then
                spotify.PlayPrev()
            ElseIf arg = "VolumeUp" Then
                spotify.VolumeUp()
            ElseIf arg = "Mute" Then
                If cmdArgs(1) = "True" Then
                    spotify.Mute(True)
                Else
                    spotify.Mute(False)
                End If

                ElseIf arg = "VolumeDown" Then
                    spotify.VolumeDown()
                ElseIf arg = "NowPlaying" Then
                    Dim a As String = spotify.Nowplaying()
                    Console.Write(a)
                ElseIf arg = "Search" Then
                    spotify.Search(cmdArgs(1), True)
                End If

                Return 0
            Else
                Return 1
            End If
    End Function

End Module
