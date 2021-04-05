import React, { useState, useEffect } from "react";
import { Table } from "react-bootstrap";
import axios from "axios";
import NavigationBar from "./NavigationBar";

const RiskManagementModifier = ({ riskManagementId }) => {
  const [result, setResult] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [data, setData] = useState(null);

  const targetUrl = `http://localhost:8080/api/riskManagements/${riskManagementId}`;

  useEffect(() => {
    fetchData();
  }, [targetUrl]);

  const fetchData = () => {
    setLoading(true);
    setError("");

    axios
      .get(targetUrl)
      .then(response => {
        console.log("Received data: ", response.data);
        setResult(response.data);
        setData({
          totalCapital: response.data.totalCapital,
          individualPositionRiskInPercent:
            response.data.individualPositionRiskInPercent
        });
        setLoading(false);
      })
      .catch(error => {
        console.log("Error: ", error);

        setLoading(false);
        setError("Error: " + error);
      });
  };

  const updateData = () => {
    setLoading(true);
    setError("");

    axios
      .put(targetUrl, data)
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

  return (
    <>
      <NavigationBar riskManagementId={riskManagementId} />
      <form>
        <Table striped bordered>
          <tbody>
            <tr>
              <td className="header">Gesamtkapital</td>
              <td>
                {" "}
                <input
                  type="text"
                  value={data.totalCapital}
                  onChange={e =>
                    setData({ ...data, totalCapital: e.target.value })
                  }
                />
              </td>
            </tr>
            <tr>
              <td className="header">Einzelpositionrisiko</td>
              <td>
                {" "}
                <input
                  type="text"
                  value={data.individualPositionRiskInPercent}
                  onChange={e =>
                    setData({
                      ...data,
                      individualPositionRiskInPercent: e.target.value
                    })
                  }
                />
              </td>
            </tr>
            <tr>
              <td />
              <td>
                {" "}
                <input
                  readOnly
                  type="text"
                  className="form-control-plaintext"
                  value={
                    (data.totalCapital * data.individualPositionRiskInPercent) /
                    100
                  }
                />
              </td>
            </tr>
            <tr>
              <td />
              <td>
                {" "}
                <button
                  type="submit"
                  className="btn btn-primary"
                  onClick={() => updateData()}
                >
                  Ändern
                </button>
              </td>
            </tr>
          </tbody>
        </Table>
      </form>
    </>
  );
};

export default RiskManagementModifier;
