using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class pooling_obj : MonoBehaviour
{
    public GameObject glasses;
    // Start is called before the first frame update
    void Start()
    {
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetKeyDown("space"))
        {
            GameObject instance = Instantiate(glasses, transform.position, transform.rotation) as GameObject;
            instance.transform.localScale = new Vector3(10f, 10f, 10f);
        }
    }
}
