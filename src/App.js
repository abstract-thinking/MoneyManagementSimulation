import React, {
  useState,
  useEffect,
  useReducer,
  Fragment,
} from 'react';
import axios from "axios";

const dataFetchReducer = (state, action) => {
  switch (action.type) {
    case 'FETCH_INIT':
      return { ...state, isLoading: true, isError: false };
    case 'FETCH_SUCCESS':
      return {
        ...state,
        isLoading: false,
        isError: false,
        data: action.payload,
      };
    case 'FETCH_FAILURE':
      return {
        ...state,
        isLoading: false,
        isError: true,
      };
    default:
      throw new Error();
  }
};

const useDataApi = (initialUrl, initialData) => {
  const [url, setUrl] = useState(initialUrl);

  const [state, dispatch] = useReducer(dataFetchReducer, {
    isLoading: false,
    isError: false,
    data: initialData,
  });

  useEffect(() => {
    let didCancel = false;

    const fetchData = async () => {
      dispatch({ type: 'FETCH_INIT' });

      try {
        const result = await axios(url);

        if (!didCancel) {
          dispatch({ type: 'FETCH_SUCCESS', payload: result.data });
        }
      } catch (error) {
        if (!didCancel) {
          dispatch({ type: 'FETCH_FAILURE' });
        }
      }
    };

    fetchData();

    return () => {
      didCancel = true;
    };
  }, [url]);

  return [state, setUrl];
};


const App = () => {

  const [{ data, isLoading, isError }, doFetch] = useDataApi(
   "http://localhost:8080/api/risk/1",
    { hits: [] },
  );

  return (
    <Fragment>

      <h1>My Risk Management</h1>

       <hr />

      <form
        onSubmit={event => {
          doFetch(
            `http://localhost:8080/api/risk/1`,
          );

          event.preventDefault();
        }}
      >
        <button type="submit">Fetch</button>
      </form>

     <hr />

      {isError && <p>Something went wrong ...</p>}

      {isLoading ? (
        <p>Loading ...</p>
      ) : (
  <div>
    <table>
    <tbody>
    <tr><td><b>Gesamtkapital</b></td><td>{data.totalCapital}</td></tr>
    <tr><td><b>Einzelpositionrisiko</b></td><td>{data.individualPositionRiskInPercent}</td></tr>
    <tr><td><b>Einzelpositionrisiko</b></td><td>{data.individualPositionRisk}</td></tr>
    <tr>
    <td><b>WKN</b></td>
    <td><b>Name</b></td>
    <td><b>Stück</b></td>
    <td><b>Kaufkurs</b></td>
    <td><b>Einstiegssumme</b></td>
    <td><b>Fiktiver Verkaufspreis</b></td>
    <td><b>Kosten</b></td>
    <td><b>Fiktiver Verkaufserlös</b></td>
    <td><b>Risiko</b></td>
    </tr>
{
data.investments && data.investments.map((investment) => {
  return(<tr>
     <td>{investment.wkn}</td>
     <td>{investment.name}</td>
     <td>{investment.quantity}</td>
     <td>{investment.purchasePrice}</td>
     <td>{investment.investment}</td>
     <td>{investment.highestNotionalSalesPrice}</td>
     <td>{investment.transactionCosts}</td>
     <td>{investment.notionalRevenue}</td>
     <td>{investment.positionRisk}</td>
    </tr>
  )})

}
    <tr>
    <td><b>Depotwert</b></td>
    <td></td>
    <td></td>
    <td>{data.totalInvestment}</td>
    <td></td>
    <td></td>
    <td></td>
    <td>{data.totalRevenue}</td><
    td>{data.depotRisk}</td></tr>
    <tr><td><b>Depotrisiko</b></td><td>{data.depotRiskInPercent}</td></tr>
    <tr><td><b>Gesamtrisiko</b></td><td>{data.totalRiskInPercent}</td></tr>
    </tbody>
    </table>


</div>
      )}
    </Fragment>
  );
};


export default App;
