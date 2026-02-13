{{/*
Common labels
*/}}
{{- define "api-gateway.labels" -}}
app: api-gateway
release: {{ .Release.Name }}
{{- end }}