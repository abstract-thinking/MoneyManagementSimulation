import React, { useState, useEffect } from "react";
import axios from "axios";

import OneViewEntry from "./OneViewEntry";
import { Table } from "react-bootstrap";
import NavigationBar from "./NavigationBar";

const OneView = ({ riskManagementId }) => {
  const [result, setResult] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const targetUrl = `http://localhost:8080/api/riskManagements/${riskManagementId}/current`;

  useEffect(() => {
    fetchData();
  }, [targetUrl]);

  const fetchData = () => {
    setLoading(true);
    setError("");

    axios
      .get(targetUrl)
      .then(response => {
        setResult(response.data);
        console.log("Received data: ", response.data);
        setLoading(false);
      })
      .catch(error => {
        console.log("Error: ", error);

        setLoading(false);
        setError("Error: " + error);
      });
  };

  if (isLoading) {
    return (
      <div
        className="position-absolute top-50 start-50 translate-middle spinner-grow"
        role="status"
      >
        <span className="visually-hidden">Loading...</span>
      </div>
    );
  }

  if (error !== "") {
    return <p>ERROR: {error}</p>;
  }

  if (result === null) {
    return <p>loading starts now</p>;
  }

  if (result.currentData.length === 0) {
    return (
      <>
        <NavigationBar riskManagementId={riskManagementId} />
        <p className="position-absolute top-50 start-50 translate-middle fs-2">
          Zur Zeit keine Investment vorhanden!
        </p>
      </>
    );
  }

  return (
    <>
      <NavigationBar riskManagementId={riskManagementId} />
      <Table striped bordered>
        <thead>
          <tr>
            <th className="header">WKN</th>
            <th className="header">Name</th>
            <th className="header">RSL</th>
            <th className="header">Preis</th>
            <th className="header">Stop Preis</th>
          </tr>
        </thead>
        <tbody>
          {result.currentData &&
            result.currentData.map(entry => (
              <OneViewEntry key={entry.name} entry={entry} />
            ))}

          <tr className="exchange-row">
            <td />
            <td className="text-content">{result.exchange}</td>
            <td className="number-content">{result.exchangeRsl.toFixed(2)}</td>
            <td colSpan={2} />
          </tr>
        </tbody>
      </Table>
    </>
  );
};

export default OneView;
