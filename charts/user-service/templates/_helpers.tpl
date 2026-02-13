{{/*
Common labels
*/}}
{{- define "user-service.labels" -}}
app: user-service
release: {{ .Release.Name }}
{{- end }}