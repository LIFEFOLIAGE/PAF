const tipoAllegati = "application/pdf, image/*, .p7m";

export default {
  "display": "form",
  "components": [
    {
      "label": "Carica ricevuta",
      "tableView": false,
      "storage": "base64",
      "webcam": false,
      "capture": false,
      "filePattern": tipoAllegati,
      "fileMaxSize": "10MB",
      "multiple": true,
      "validate": {
        "required": true,
        "customMessage": "Devi caricare almeno una ricevuta"
      },
      "key": "ricevute",
      "type": "file",
      "input": true
    }
  ]
};
