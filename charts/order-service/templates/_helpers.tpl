{{/*
Common labels
*/}}
{{- define "order-service.labels" -}}
app: order-service
release: {{ .Release.Name }}
{{- end }}