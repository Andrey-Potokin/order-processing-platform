{{/*
Common labels
*/}}
{{- define "inventory-service.labels" -}}
app: inventory-service
release: {{ .Release.Name }}
{{- end }}