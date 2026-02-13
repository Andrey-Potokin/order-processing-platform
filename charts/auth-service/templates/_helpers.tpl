{{/*
Common labels
*/}}
{{- define "auth-service.labels" -}}
app: auth-service
release: {{ .Release.Name }}
{{- end }}