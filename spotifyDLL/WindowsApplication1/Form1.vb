Imports spotify


Public Class Form1
    Private Sub Button1_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button1.Click
        Dim a As String = spotify.Nowplaying()
        Label1.Text = a



    End Sub
End Class
