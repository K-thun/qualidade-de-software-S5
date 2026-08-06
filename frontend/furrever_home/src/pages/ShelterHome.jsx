import React, { useEffect, useState } from 'react';

import PetsTable from '../components/Shelter/PetsTable';
import { readLocalStorage, saveLocalStorage } from '../utils/helper';
import { getPetsForShelter, getShelterProfile } from '../service/api/shelterService';

const ShelterHome = ({ children }) => {

  const [search, setSearch] = useState('');

  const [change, setChange] = useState(false)
  const [pets, setPets] = useState([])
  const sid = readLocalStorage("shelterID");
  const id = readLocalStorage("id");


  const getPet = () => {
    getPetsForShelter(sid)
      .then(response => {
        setPets(response.data)



      })
      .catch(error => {
        console.log(error);
      })
  }

  useEffect(() => {

    getShelterProfile(id)
      .then(response => {
        saveLocalStorage("User", JSON.stringify(response.data));
      })
      .catch(error => {
        console.log(error);
      })

    getPet()

  }, [change])



  return (
    <section>

      <div className='lg:flex '>

        {/* <div className='lg:w-[20%]'>
          <Sidebar />
        </div> */}



        <div className=' sm:w-full'>

          <PetsTable pets={pets} setChange={setChange}/>

        </div>

      </div>
    </section>
  )
}

export default ShelterHome
