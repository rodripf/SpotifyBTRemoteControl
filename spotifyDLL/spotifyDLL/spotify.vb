' Spotify API V0.01 beta - a very quick First draft
' © august 3 2009 by Steffest
' This code is free to use in any way you want and comes with NO WARRANTIES
' tested with Spotify 0.3.18
' Usage:
' 
' Dim Spotify As New Spotify()
' 
' Spotify.PlayPause()
' Spotify.PlayPrev()
' Spotify.PlayNext()
' Spotify.Mute()
' Spotify.VolumeUp()
' Spotify.VolumeDown()
' Spotify.Nowplaying() (Gets the name of the current playing track)
' Spotify.Search("Artist",False) (Searches for "Artist")
' Spotify.Search("Artist",True) (Searches for "Artist" and starts playing the results)



Public Module spotify

#Region " win32 "
    Private Declare Auto Function FindWindow Lib "user32" (ByVal lpClassName As String, ByVal lpWindowName As String) As IntPtr
    Private Declare Auto Function SendMessage Lib "user32" (ByVal hWnd As IntPtr, ByVal Msg As UInteger, ByVal wParam As IntPtr, ByVal lParam As IntPtr) As IntPtr
    Private Declare Auto Function SetForegroundWindow Lib "user32" (ByVal hWnd As IntPtr) As Boolean
    Private Declare Auto Function keybd_event Lib "user32" (ByVal bVk As Byte, ByVal bScan As Byte, ByVal dwFlags As Integer, ByVal dwExtraInfo As Integer) As Boolean
    Private Declare Sub Sleep Lib "kernel32.dll" (ByVal Milliseconds As Integer)
    Private Declare Auto Function GetWindowText Lib "user32" (ByVal hwnd As IntPtr, ByVal lpString As String, ByVal cch As IntPtr) As IntPtr
    Private Declare Auto Function SetWindowText Lib "user32" (ByVal hwnd As IntPtr, ByVal lpString As String) As Boolean
    Private Declare Auto Function EnumChildWindows Lib "user32" (ByVal hWndParent As Long, ByVal lpEnumFunc As Long, ByVal lParam As Long) As Long
#End Region

#Region " constants "
    Private Const WM_KEYDOWN = &H100
    Private Const WM_KEYUP = &H101
    Private Const WM_MOUSEACTIVATE = &H21
    Private Const KEYEVENTF_EXTENDEDKEY As Integer = &H1S
    Private Const KEYEVENTF_KEYUP As Integer = &H2S
#End Region

    Private w = FindWindow("SpotifyMainWindow", vbNullString)


    Public Function PlayPause() As Boolean
        SendMessage(w, WM_KEYDOWN, System.Windows.Forms.Keys.Space, 0)
        SendMessage(w, WM_KEYUP, System.Windows.Forms.Keys.Space, 0)
    End Function

    Public Function PlayPrev() As Boolean
        ' for some reason the PostMessage(w, WM_KEYDOWN, System.Windows.Forms.Keys.MediaNextTrack, 0) doesn't work
        ' sending ctrl+ commands to a windows still is a PITA ...
        SetForegroundWindow(w)
        keybd_event(System.Windows.Forms.Keys.ControlKey, &H1D, 0, 0)
        keybd_event(System.Windows.Forms.Keys.Left, &H45S, KEYEVENTF_EXTENDEDKEY Or 0, 0)
        keybd_event(System.Windows.Forms.Keys.Left, &H45S, KEYEVENTF_EXTENDEDKEY Or KEYEVENTF_KEYUP, 0)
        Sleep(100) ' wait until spotify has trapped the control key before releasing it
        keybd_event(System.Windows.Forms.Keys.ControlKey, &H1D, KEYEVENTF_KEYUP, 0)
    End Function

    Public Function PlayNext() As Boolean
        SetForegroundWindow(w)
        keybd_event(System.Windows.Forms.Keys.ControlKey, &H1D, 0, 0)
        keybd_event(System.Windows.Forms.Keys.Right, &H45S, KEYEVENTF_EXTENDEDKEY Or 0, 0)
        keybd_event(System.Windows.Forms.Keys.Right, &H45S, KEYEVENTF_EXTENDEDKEY Or KEYEVENTF_KEYUP, 0)
        Try
            Sleep(100) ' wait until spotify has trapped the control key before releasing it
        Catch ex As Exception

        End Try

        keybd_event(System.Windows.Forms.Keys.ControlKey, &H1D, KEYEVENTF_KEYUP, 0)
    End Function

    Public Function VolumeUp() As Boolean
        SetForegroundWindow(w)
        keybd_event(System.Windows.Forms.Keys.ControlKey, &H1D, 0, 0)
        keybd_event(System.Windows.Forms.Keys.Up, &H45S, KEYEVENTF_EXTENDEDKEY Or 0, 0)
        keybd_event(System.Windows.Forms.Keys.Up, &H45S, KEYEVENTF_EXTENDEDKEY Or KEYEVENTF_KEYUP, 0)
        Sleep(100) ' wait until spotify has trapped the control key before releasing it
        keybd_event(System.Windows.Forms.Keys.ControlKey, &H1D, KEYEVENTF_KEYUP, 0)
    End Function



    Public Function Mute(ByVal muted As Boolean) As Boolean
        If muted Then
            For i = 0 To 10
                VolumeUp()
            Next
        Else
            For i = 0 To 10
                VolumeDown()
            Next
        End If
    End Function

    Public Function VolumeDown() As Boolean
        SetForegroundWindow(w)
        keybd_event(System.Windows.Forms.Keys.ControlKey, &H1D, 0, 0)
        keybd_event(System.Windows.Forms.Keys.Down, &H45S, KEYEVENTF_EXTENDEDKEY Or 0, 0)
        keybd_event(System.Windows.Forms.Keys.Down, &H45S, KEYEVENTF_EXTENDEDKEY Or KEYEVENTF_KEYUP, 0)
        Sleep(100) ' wait until spotify has trapped the control key before releasing it
        keybd_event(System.Windows.Forms.Keys.ControlKey, &H1D, KEYEVENTF_KEYUP, 0)
    End Function

    Public Function Nowplaying() As String
        Dim lpText As String
        lpText = New String(Chr(0), 100)
        Dim intLength As Integer = GetWindowText(w, lpText, lpText.Length)
        If (intLength <= 0) OrElse (intLength > lpText.Length) Then Return "Unknown"
        Dim strTitle As String = lpText.Substring(0, intLength)
        strTitle = Mid(strTitle, 11)
        Return strTitle
    End Function

    Public Function Search(ByVal s As String, ByVal AndPlay As Boolean) As Boolean
        SetForegroundWindow(w)
        keybd_event(System.Windows.Forms.Keys.ControlKey, &H1D, 0, 0)
        keybd_event(System.Windows.Forms.Keys.L, &H45S, KEYEVENTF_EXTENDEDKEY Or 0, 0)
        keybd_event(System.Windows.Forms.Keys.L, &H45S, KEYEVENTF_EXTENDEDKEY Or KEYEVENTF_KEYUP, 0)
        Sleep(100) ' wait until spotify has trapped the control key before releasing it
        keybd_event(System.Windows.Forms.Keys.ControlKey, &H1D, KEYEVENTF_KEYUP, 0)

        Sleep(200)
        System.Windows.Forms.SendKeys.SendWait(s & Chr(13))

        If AndPlay Then
            ' this is a bit stupid but works in this version: press tab twice, then enter
            Sleep(2000)
            System.Windows.Forms.SendKeys.SendWait(System.Windows.Forms.Keys.Tab)
            Sleep(200)
            System.Windows.Forms.SendKeys.SendWait(System.Windows.Forms.Keys.Tab)
            Sleep(500)

            SendMessage(w, WM_KEYDOWN, System.Windows.Forms.Keys.Enter, 0)
            SendMessage(w, WM_KEYUP, System.Windows.Forms.Keys.Enter, 0)

        End If

    End Function

End Module
