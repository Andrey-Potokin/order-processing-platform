{{/*
Common labels
*/}}
{{- define "notification-service.labels" -}}
app: notification-service
release: {{ .Release.Name }}
{{- end }}