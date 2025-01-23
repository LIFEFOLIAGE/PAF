const tipoAllegati = "application/pdf, image/*, .p7m";

export default {
  "display": "form",
  "components": [
    {
      "label": "Durata proroga (mesi)",
      "applyMaskOn": "change",
      "mask": false,
      "tableView": false,
      "delimiter": false,
      "requireDecimal": false,
      "inputFormat": "plain",
      "truncateMultipleSpaces": false,
      "validate": {
        "required": true,
        "min": 0,
        "max": 12,
        "customMessage": "La durata della proroga non può superare i 12 mesi"
      },
      "key": "durataProroga",
      "type": "number",
      "decimalLimit": 0,
      "input": true
    },
    {
      "label": "Motivazione",
      "applyMaskOn": "change",
      "autoExpand": false,
      "tableView": true,
      "validate": {
        "required": true,
        "customMessage": "La motivazione non puo essere vuota"
      },
      "key": "motivazione",
      "type": "textarea",
      "input": true
    },
    {
      "label": "Carica ricevuta",
      "tableView": false,
      "storage": "base64",
      "webcam": false,
      "capture": false,
      "filePattern": tipoAllegati,
      "fileMaxSize": "10MB",
      "validate": {
        "required": true,
        "customMessage": "Devi caricare almeno una ricevuta"
      },
      "key": "bollo",
      "type": "file",
      "input": true
    }
  ]
};
