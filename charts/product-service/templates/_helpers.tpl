{{/*
Common labels
*/}}
{{- define "product-service.labels" -}}
app: product-service
release: {{ .Release.Name }}
{{- end }}