/**
 * cordova is available under *either* the terms of the modified BSD license *or* the
 * MIT License (2008). See http://opensource.org/licenses/alphabetical for full text.
 *
 * Copyright (c) Matt Kane 2010
 * Copyright (c) 2011, IBM Corporation
 */


        var exec = require("cordova/exec");

        /**
         * Constructor.
         *
         * @returns {OwnPassBarcodeScanner}
         */
        function OwnPassBarcodeScanner() {

            /**
             * Encoding constants.
             *
             * @type Object
             */
            this.Encode = {
                TEXT_TYPE: "TEXT_TYPE",
                EMAIL_TYPE: "EMAIL_TYPE",
                PHONE_TYPE: "PHONE_TYPE",
                SMS_TYPE: "SMS_TYPE"
                //  CONTACT_TYPE: "CONTACT_TYPE",  // TODO:  not implemented, requires passing a Bundle class from Javascript to Java
                //  LOCATION_TYPE: "LOCATION_TYPE" // TODO:  not implemented, requires passing a Bundle class from Javascript to Java
            };
        };

        /**
         * Read code from scanner.
         *
         * @param {Function} successCallback This function will recieve a result object: {
         *        text : '12345-mock',    // The code that was scanned.
         *        format : 'FORMAT_NAME', // Code format.
         *        cancelled : true/false, // Was canceled.
         *    }
         * @param {Function} errorCallback
         */
        OwnPassBarcodeScanner.prototype.scan = function (successCallback, errorCallback) {
            if (errorCallback == null) {
                errorCallback = function () {
                };
            }

            if (typeof errorCallback != "function") {
                console.log("OwnPassBarcodeScanner.scan failure: failure parameter not a function");
                return;
            }

            if (typeof successCallback != "function") {
                console.log("OwnPassBarcodeScanner.scan failure: success callback parameter must be a function");
                return;
            }

            exec(successCallback, errorCallback, 'OwnPassBarcodeScanner', 'scan', []);
        };

        //-------------------------------------------------------------------
        OwnPassBarcodeScanner.prototype.encode = function (type, data, successCallback, errorCallback, options) {
            if (errorCallback == null) {
                errorCallback = function () {
                };
            }

            if (typeof errorCallback != "function") {
                console.log("OwnPassBarcodeScanner.encode failure: failure parameter not a function");
                return;
            }

            if (typeof successCallback != "function") {
                console.log("OwnPassBarcodeScanner.encode failure: success callback parameter must be a function");
                return;
            }

            exec(successCallback, errorCallback, 'OwnPassBarcodeScanner', 'encode', [
                {"type": type, "data": data, "options": options}
            ]);
        };

        var OwnPassBarcodeScanner = new OwnPassBarcodeScanner();
        module.exports = OwnPassBarcodeScanner;
